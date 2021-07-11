package org.ionproject.integration.domain.timetable

import com.squareup.moshi.Types
import org.ionproject.integration.domain.common.Language
import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.common.Weekday
import org.ionproject.integration.domain.exception.TimetableTeachersBuilderException
import org.ionproject.integration.domain.timetable.dto.FacultyDTO
import org.ionproject.integration.domain.timetable.dto.FacultyRawData
import org.ionproject.integration.domain.timetable.dto.RawTimetableData
import org.ionproject.integration.domain.timetable.dto.toDto
import org.ionproject.integration.domain.timetable.model.ClassDetail
import org.ionproject.integration.domain.timetable.model.Course
import org.ionproject.integration.domain.timetable.model.CourseTeacher
import org.ionproject.integration.domain.timetable.model.Faculty
import org.ionproject.integration.domain.timetable.model.Label
import org.ionproject.integration.domain.timetable.model.RecurrentEvent
import org.ionproject.integration.infrastructure.DateUtils
import org.ionproject.integration.infrastructure.Try
import org.ionproject.integration.infrastructure.orThrow
import org.ionproject.integration.infrastructure.pdfextractor.tabula.Cell
import org.ionproject.integration.infrastructure.pdfextractor.tabula.Table
import org.ionproject.integration.infrastructure.text.IgnoredWords
import org.ionproject.integration.infrastructure.text.JsonUtils
import org.ionproject.integration.infrastructure.text.RegexUtils
import org.ionproject.integration.infrastructure.text.generateAcronym
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime

class IselTimetableTeachersBuilder : ITimetableTeachersBuilder<RawTimetableData> {
    companion object {
        private const val CALENDAR_TERM_REGEX =
            """\b(\s?Ano\sLetivo\s?:\s?)(\d{2,4}\/\d{2,4})\s?-\s?(Verão|Inverno)\b"""
        private const val SCHOOL_REGEX = """\A.*"""
        private const val PROGRAMME_REGEX = """^(Licenciatura|Mestrado).*$"""
        private const val CLASS_SECTION_REGEX = """\bTurma\s?:\s?([LM][A-Z+]+\d{1,2}\w+)\b"""
        private const val TIME_SLOT_REGEX = """([8-9]|1[0-9]|2[0-3]).([03])0"""
        private const val HEIGHT_ONE_HALF_HOUR_THRESHOLD = 58
        private const val HEIGHT_HALF_HOUR_THRESHOLD = 20
        private const val YEAR_OFFSET = 2000 // Add to term years with only two digits (i.e. 20/21 instead of 2020/2021)
    }

    private var iselTimetableTeachers = Try.of { TimetableTeachers() }

    override fun setTimetable(rawData: RawTimetableData) {
        iselTimetableTeachers.map {
            if (it.timetable.count() == 0 || it.teachers.count() == 0)
                rawDataToBusiness(rawData)
        }
    }

    override fun setTeachers(rawData: RawTimetableData) {
        setTimetable(rawData)
    }

    fun getTimetableTeachers(): Try<TimetableTeachers> {
        return iselTimetableTeachers.map { it.copy() }
    }

    private fun rawDataToBusiness(rawTimetableData: RawTimetableData) {
        fun String.toTableList(): Try<List<Table>> =
            JsonUtils.fromJson(this, Types.newParameterizedType(List::class.java, Table::class.java))

        val instructorJson = rawTimetableData.instructorData.toTableList().orThrow()
        rawTimetableData.scheduleData.toTableList().map { mapTablesToBusiness(rawTimetableData, it, instructorJson) }
    }

    private fun mapTablesToBusiness(
        rawTimetableData: RawTimetableData,
        tableList: List<Table>,
        instructorList: List<Table>
    ) {
        iselTimetableTeachers = Try.of {
            val timetableList = mutableListOf<Timetable>()
            val teacherList = mutableListOf<CourseTeacher>()

            rawTimetableData.textData.forEachIndexed { idx, data ->
                val timetable = Timetable(creationDateTime = rawTimetableData.creationDate)
                val courseTeacher = CourseTeacher()

                setCommonData(data, timetable, courseTeacher)

                timetable.courses = getCourseList(tableList[idx].data)
                courseTeacher.courses = getFacultyList(instructorList[idx].data)

                timetableList.add(timetable)
                teacherList.add(courseTeacher)
            }

            TimetableTeachers(
                timetableList,
                teacherList
            )
        }
    }

    private fun setCommonData(data: String, timetable: Timetable, courseTeacher: CourseTeacher) {
        val retrievalDateTime = DateUtils.formatToISO8601(ZonedDateTime.now())
        val schoolText = RegexUtils.findMatches(SCHOOL_REGEX, data)[0].trimEnd()
        val programmeText = RegexUtils.findMatches(PROGRAMME_REGEX, data, RegexOption.MULTILINE)[0].trimEnd()
        val calendarTerm = getCalendarTerm(data)
        val classSection = getSection(data)
        val schoolAcr = generateAcronym(schoolText, IgnoredWords.of(Language.PT))
        val programmeArc = generateAcronym(programmeText, IgnoredWords.of(Language.PT))

        val school = School(name = schoolText, acr = schoolAcr)
        val programme = Programme(name = programmeText, acr = programmeArc)

        timetable.retrievalDateTime = retrievalDateTime
        timetable.school = school
        timetable.programme = programme
        timetable.calendarTerm = calendarTerm
        timetable.calendarSection = classSection
        timetable.curricularTerm = getCurricularTermFromSection(classSection)

        courseTeacher.school = school
        courseTeacher.programme = programme
        courseTeacher.calendarTerm = calendarTerm
        courseTeacher.calendarSection = classSection
    }

    private fun getCourseList(data: Array<Array<Cell>>): List<Course> {
        val weekdays: Map<Double, Weekday> = populateWeekdays(data.first())
        return data
            .drop(1) // First row already read to populate weekdays
            .flatMap { cells ->
                val cleanCells = cells.filter { it.isVisible() }
                val classStartTime = getBeginTime(cleanCells.first())

                // Drop first cell again as it was used to get class start time
                getCourseList(cleanCells.drop(1), weekdays, classStartTime)
            }
    }

    private data class EventDuration(val beginTime: LocalTime, val duration: Duration)

    private fun getCourseList(cells: List<Cell>, weekdays: Map<Double, Weekday>, beginTime: LocalTime): List<Course> {
        return cells.flatMap { cell ->
            val weekday = weekdays.getOrElse(cell.left) {
                throw TimetableTeachersBuilderException("No matching weekday cell found for ${cell.left}")
            }
            val duration = EventDuration(beginTime, getDuration(cell))
            getAllCourseDataFromCell(cell, weekday, duration)
        }
    }

    private fun getAllCourseDataFromCell(cell: Cell, weekday: Weekday, duration: EventDuration): List<Course> =
        cell.text.split('\r')
            .map {
                val cellText = if (it.contains('[')) it else cell.text
                getCourseDataFromCellText(cellText, weekday, duration)
            }

    private fun getCourseDataFromCellText(text: String, weekday: Weekday, eventDuration: EventDuration): Course {
        val courseDetails = ClassDetail.from(text)
        val acr = courseDetails.acronym
        return Course(
            label = Label(acr = acr),
            events = listOf(
                RecurrentEvent(
                    title = null,
                    category = courseDetails.type,
                    location = listOf(courseDetails.location),
                    beginTime = eventDuration.beginTime.toString(),
                    duration = String.format(
                        "%02d:%02d",
                        eventDuration.duration.toHoursPart(),
                        eventDuration.duration.toMinutesPart()
                    ),
                    weekdays = listOf(weekday)
                )
            )
        )
    }

    private fun getCalendarTerm(rawData: String): String {
        if (!RegexUtils.isMatch(CALENDAR_TERM_REGEX, rawData))
            throw IllegalArgumentException("Invalid calendar term: $rawData")

        val (_, _, termYears, termSemester) = RegexUtils.findMatches(CALENDAR_TERM_REGEX, rawData)

        val (startYear, endYear) = getYearsFromText(termYears)
        val termNumber = getTermNumberFromSemesterText(termSemester)

        return "$startYear-$endYear-$termNumber"
    }

    private fun getSection(rawData: String): String {
        if (!RegexUtils.isMatch(CLASS_SECTION_REGEX, rawData))
            throw IllegalArgumentException("Invalid class section: $rawData")

        val (_, section) = RegexUtils.findMatches(CLASS_SECTION_REGEX, rawData)

        return section
    }

    private fun getDuration(cell: Cell): Duration = when {
        cell.height > HEIGHT_ONE_HALF_HOUR_THRESHOLD -> {
            Duration.ofHours(3)
        }
        cell.height > HEIGHT_HALF_HOUR_THRESHOLD -> {
            Duration.ofHours(1).plusMinutes(30)
        }
        else -> {
            Duration.ofMinutes(30)
        }
    }

    private fun getFacultyList(data: Array<Array<Cell>>): List<Faculty> =
        getRawFacultyList(data)
            .map(FacultyRawData::toDto)
            .filter(FacultyDTO::isValid)
            .groupBy(keySelector = FacultyDTO::classDetail, valueTransform = FacultyDTO::instructor)
            .map { Faculty(it.key, it.value) }

    /*
        Course text may be empty when multiple consecutive instructors teach the same course.
        Only the first will have course text, it will be empty for subsequent instructors.
        This function hides this detail by filling in empty course text with the correct value.
    */
    private fun getRawFacultyList(data: Array<Array<Cell>>): List<FacultyRawData> {
        require(data.all { it.size >= 2 }) // Always expect at least two cells (course + instructor)
        require(data.first().first().text.isNotBlank()) // First cell of first row must have course text

        var prevCourse = ""
        val left = data.map { cell ->
            val courseText = cell.first().text.ifEmpty { prevCourse }
            val instructorText = cell[1].text
            prevCourse = courseText

            FacultyRawData(courseText, instructorText)
        }

        val right = data.filter { it.size == 4 } // Filter out rows that have no right-hand side cells
            .map { cell ->
                val courseText = cell[2].text.ifEmpty { prevCourse }
                val instructorText = cell[3].text
                prevCourse = courseText

                FacultyRawData(courseText, instructorText)
            }

        return left + right
    }

    private fun populateWeekdays(cells: Array<Cell>): Map<Double, Weekday> =
        cells.filter(Cell::isVisible)
            .associateBy(Cell::left) { Weekday.fromPortuguese(it.text) }

    private fun getBeginTime(cell: Cell): LocalTime {
        val matches = RegexUtils.findMatches(TIME_SLOT_REGEX, cell.text)
        val time = matches[0].split('.')

        return LocalTime.of(time[0].toInt(), time[1].toInt())
    }

    private fun getYearsFromText(data: String): Pair<Int, Int> {
        val (first, second) = data.split("/")
            .map(String::toInt)
            .map { if (it < YEAR_OFFSET) it + YEAR_OFFSET else it }

        return first to second
    }

    private fun getTermNumberFromSemesterText(semesterText: String): Int = when (semesterText) {
        "Inverno" -> 1
        "Verão" -> 2
        else -> throw IllegalArgumentException("Invalid term description: $semesterText")
    }
}
