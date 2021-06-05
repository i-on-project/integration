package org.ionproject.integration.builder.implementations

import com.squareup.moshi.Types
import org.ionproject.integration.builder.exceptions.TimetableTeachersBuilderException
import org.ionproject.integration.builder.interfaces.ITimetableTeachersBuilder
import org.ionproject.integration.model.external.timetable.Course
import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.EventCategory
import org.ionproject.integration.model.external.timetable.Faculty
import org.ionproject.integration.model.external.timetable.Label
import org.ionproject.integration.model.external.timetable.Language
import org.ionproject.integration.model.external.timetable.Programme
import org.ionproject.integration.model.external.timetable.RecurrentEvent
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.external.timetable.TimetableTeachers
import org.ionproject.integration.model.external.timetable.Weekday
import org.ionproject.integration.model.internal.tabula.Cell
import org.ionproject.integration.model.internal.tabula.Table
import org.ionproject.integration.model.internal.timetable.isel.RawTimetableData
import org.ionproject.integration.utils.DateFormat
import org.ionproject.integration.utils.IgnoredWords
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.RegexUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.generateAcronym
import org.ionproject.integration.utils.orThrow
import java.time.Duration
import java.time.LocalTime
import java.util.Date

class IselTimetableTeachersBuilder : ITimetableTeachersBuilder<RawTimetableData> {
    companion object {
        private const val SCHOOL_REGEX = "\\A.*"
        private const val PROGRAMME_REGEX = "^(Licenciatura|Mestrado).*$"
        private const val CLASS_SECTION_REGEX = "\\sTurma\\s?:\\s?[LM][A-Z+]+\\d{1,2}\\w+"
        private const val CALENDAR_TERM_REGEX = "(\\sAno\\sLetivo\\s?:\\s?)(.+?(\\r|\\R))"
        private const val TIME_SLOT_REGEX = "([8-9]|1[0-9]|2[0-3]).([03])0"
        private const val HEIGHT_ONE_HALF_HOUR_THRESHOLD = 58
        private const val HEIGHT_HALF_HOUR_THRESHOLD = 20
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
        val retrievalDateTime = DateFormat.format(Date())
        val school = RegexUtils.findMatches(SCHOOL_REGEX, data)[0].trimEnd()
        val programme = RegexUtils.findMatches(PROGRAMME_REGEX, data, RegexOption.MULTILINE)[0].trimEnd()
        val calendarTerm = RegexUtils.findMatches(CALENDAR_TERM_REGEX, data, RegexOption.MULTILINE)[0]
            .replace("Ano Letivo :", "")
            .trim()
            .let {
                normalizeTerm(it)
            }
        val classSection =
            RegexUtils.findMatches(CLASS_SECTION_REGEX, data, RegexOption.MULTILINE)[0].replace("Turma :", "").trim()
        val schoolAcr = generateAcronym(school, IgnoredWords.of(Language.PT))
        val programmeArc = generateAcronym(programme, IgnoredWords.of(Language.PT))

        timetable.retrievalDateTime = retrievalDateTime
        timetable.school = School(name = school, acr = schoolAcr)
        timetable.programme = Programme(name = programme, acr = programmeArc)
        timetable.calendarTerm = calendarTerm
        timetable.calendarSection = classSection

        courseTeacher.school = School(name = school, acr = schoolAcr)
        courseTeacher.programme = Programme(name = programme, acr = programmeArc)
        courseTeacher.calendarTerm = calendarTerm
        courseTeacher.calendarSection = classSection
    }

    private fun normalizeTerm(raw: String): String {
        val startYear = raw.take(4).toInt()
        val termType = raw.substringAfter('-')

        val termNumber = when (termType) {
            "Inverno" -> 1
            "Verão" -> 2
            else -> throw IllegalArgumentException("Invalid term description: $termType")
        }

        return "$startYear-${startYear + 1}-$termNumber"
    }

    private fun getCourseList(data: Array<Array<Cell>>): List<Course> {
        val courseList = mutableListOf<Course>()
        val weekdays: Map<Double, Weekday> = populateWeekdays(data.first())

        data.drop(1).forEach { cells ->
            val cleanCells = cells.filter { it.isVisible() }
            val classStartTime = getBeginTime(cleanCells.first())

            cleanCells.drop(1).forEach { cell ->
                val weekday = weekdays.getOrElse(cell.left) {
                    throw TimetableTeachersBuilderException("No matching weekday cell found for ${cell.left}")
                }
                val duration = EventDuration(classStartTime, getDuration(cell))
                courseList += getAllCourseDataFromCell(cell, weekday, duration)
            }
        }

        return courseList
    }

    private data class EventDuration(val beginTime: LocalTime, val duration: Duration)

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
                    description = "${getDescription(courseDetails.type)}$acr",
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

    private fun isTimeslot(cell: Cell): Boolean = TIME_SLOT_REGEX.toRegex().containsMatchIn(cell.text)

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

    // TODO the description is no longer necessary under this new data model. To confirm with team
    private fun getDescription(classType: EventCategory) = when (classType) {
        EventCategory.LECTURE -> "Aulas Teóricas de "
        EventCategory.PRACTICE -> "Aulas Práticas de "
        EventCategory.LAB -> "Aulas Laboratório de "
        EventCategory.LECTURE_PRACTICE -> "Aulas Teórico-práticas de "
    }
}
