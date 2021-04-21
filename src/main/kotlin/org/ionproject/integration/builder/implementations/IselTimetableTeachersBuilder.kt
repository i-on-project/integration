package org.ionproject.integration.builder.implementations

import com.squareup.moshi.Types
import java.time.Duration
import java.time.LocalTime
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
import org.ionproject.integration.model.external.timetable.Instructor
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.external.timetable.TimetableTeachers
import org.ionproject.integration.model.external.timetable.Weekdays
import org.ionproject.integration.model.internal.tabula.Cell
import org.ionproject.integration.model.internal.tabula.Table
import org.ionproject.integration.model.internal.timetable.isel.ProgrammeMap
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.RegexUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow

class IselTimetableTeachersBuilder : ITimetableTeachersBuilder<RawData> {
    companion object {
        private const val SCHOOL_REGEX = "\\A.*"
        private const val PROGRAMME_REGEX = "^(Licenciatura|Mestrado).*$"
        private const val CLASS_SECTION_REGEX = "\\sTurma\\s?:\\s?[LM][A-Z+]+\\d{1,2}\\w+"
        private const val CALENDAR_TERM_REGEX = "(\\sAno\\sLetivo\\s?:\\s?)(.+?(\\r|\\R))"
        private const val TIME_SLOT_REGEX = "([8-9]|1[0-9]|2[0-3]).(0|3)0"
        private const val HEIGHT_ONE_HALF_HOUR_THRESHOLD = 58
        private const val HEIGHT_HALF_HOUR_THRESHOLD = 20
    }

    private var iselTimetableTeachers = Try.of { TimetableTeachers() }

    override fun setTimetable(rawData: RawData) {
        iselTimetableTeachers.map {
            if (it.timetable.count() == 0 || it.teachers.count() == 0)
                rawDataToBusiness(rawData)
        }
    }

    override fun setTeachers(rawData: RawData) {
        setTimetable(rawData)
    }

    fun getTimetableTeachers(): Try<TimetableTeachers> {
        return iselTimetableTeachers.map { it.copy() }
    }

    private fun rawDataToBusiness(rawData: RawData) {
        val instructorJson = JsonUtils.fromJson<List<Table>>(
            rawData.instructors,
            Types.newParameterizedType(List::class.java, Table::class.java)
        ).orThrow()
        JsonUtils.fromJson<List<Table>>(
            rawData.jsonData,
            Types.newParameterizedType(List::class.java, Table::class.java)
        )
            .map { mapTablesToBusiness(rawData, it, instructorJson) }
    }

    private fun mapTablesToBusiness(rawData: RawData, tableList: List<Table>, instructorList: List<Table>) {
        iselTimetableTeachers = Try.of {
            val timetableList = mutableListOf<Timetable>()
            val teacherList = mutableListOf<CourseTeacher>()

            rawData.textData.forEachIndexed { i, data ->
                val timetable = Timetable()
                val courseTeacher = CourseTeacher()

                setCommonData(data, timetable, courseTeacher)

                timetable.courses = getCourseList(tableList[i].data)
                courseTeacher.courses = getFacultyList(instructorList[i].data)

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
        val school = RegexUtils.findMatches(SCHOOL_REGEX, data)[0].trimEnd()
        val programme = RegexUtils.findMatches(PROGRAMME_REGEX, data, RegexOption.MULTILINE)[0].trimEnd()
        val calendarTerm =
            RegexUtils.findMatches(CALENDAR_TERM_REGEX, data, RegexOption.MULTILINE)[0].replace("Ano Letivo :", "")
                .trim()
        val classSection =
            RegexUtils.findMatches(CLASS_SECTION_REGEX, data, RegexOption.MULTILINE)[0].replace("Turma :", "").trim()
        val schoolAcr = "ISEL"
        val programmeArc = ProgrammeMap.map[programme].toString()

        timetable.school = School(name = school, acr = schoolAcr)
        timetable.programme = Programme(name = programme, acr = programmeArc)
        timetable.calendarTerm = calendarTerm
        timetable.calendarSection = classSection
        timetable.language = Language.PT.value

        courseTeacher.school = School(name = school, acr = schoolAcr)
        courseTeacher.programme = Programme(name = programme, acr = programmeArc)
        courseTeacher.calendarTerm = calendarTerm
        courseTeacher.calendarSection = classSection
        courseTeacher.language = Language.PT.value
    }

    private fun getCourseList(data: Array<Array<Cell>>): List<Course> {
        var courseList = mutableListOf<Course>()
        var weekdays = mutableMapOf<Double, String>()
        var courseDetails: ClassDetail

        for (i in 0 until data.count()) {
            val cells = data[i]

            if (weekdays.keys.isEmpty()) {
                populateWeekdays(cells, weekdays)
                continue
            }

            var beginTime = LocalTime.now()

            for (j in 0 until cells.count()) {
                val cell = cells[j]

                if (cell.text.isEmpty()) continue

                val matches = RegexUtils.findMatches(TIME_SLOT_REGEX, cells[j].text)
                if (matches.count() != 0) {
                    beginTime = getBeginTime(matches)
                    continue
                }

                cell.text.split('\r')
                    .forEach {
                        val cellText = if (it.contains('[')) it else cell.text

                        courseDetails = ClassDetail.from(cellText)

                        var duration: Duration = when {
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

                        if (!weekdays.contains(cell.left)) throw TimetableTeachersBuilderException("Can't find weekday")

                        val acr = courseDetails.acronym
                        courseList.add(
                            Course(
                                label = Label(acr = acr),
                                events = listOf(
                                    RecurrentEvent(
                                        title = null,
                                        description = "${getDescription(courseDetails.type)}$acr",
                                        category = EventCategory.LECTURE.value,
                                        location = listOf(courseDetails.location),
                                        beginTime = beginTime.toString(),
                                        duration = String.format(
                                            "%02d:%02d",
                                            duration.toHoursPart(),
                                            duration.toMinutesPart()
                                        ),
                                        weekday = listOf(weekdays.getOrDefault(cell.left, ""))
                                    )
                                )
                            )
                        )
                    }
            }
        }

        return courseList
    }

    private fun getFacultyList(data: Array<Array<Cell>>): List<Faculty> =
        getRawFacultyList(data).map { rawData ->
            val classDetail = ClassDetail.from(rawData.courseText)
            val instructor = Instructor(rawData.instructorText)

            return@map classDetail to instructor
        }.groupBy(keySelector = { it.first }, valueTransform = { it.second })
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

    private fun populateWeekdays(cells: Array<Cell>, weekdays: MutableMap<Double, String>) {
        for (i in 0 until cells.count()) {
            if (cells[i].width == 0.0 || cells[i].height == 0.0 || cells[i].text.isEmpty()) continue
            weekdays[cells[i].left] =
                Weekdays.values().first { it.toPortuguese() == cells[i].text.toUpperCase() }.toShortString()
        }
    }

    private fun getBeginTime(matches: List<String>): LocalTime? {
        val time = matches[0]
            .split('.')

        return LocalTime
            .of(time[0].toInt(), time[1].toInt())
    }

    private fun getDescription(classType: String) = when (classType) {
        "T" -> "Aulas Teóricas de "
        "P" -> "Aulas Práticas de "
        "L" -> "Aulas Laboratório de "
        "T/P" -> "Aulas Teórico-práticas de "
        else -> ""
    }
}
