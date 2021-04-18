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
import org.ionproject.integration.model.external.timetable.Teacher
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
        private const val HEIGHT_ONE_HALF_HOUR_THRESHOLD = 47
        private const val HEIGHT_HALF_HOUR_THRESHOLD = 17
    }

    private var iselTimetableTeachers = Try.of { TimetableTeachers() }

    override fun setTimetable(rawData: RawData) {
        iselTimetableTeachers
            .map { timetableTeachers ->
                if (timetableTeachers.timetable.count() == 0 || timetableTeachers.teachers.count() == 0) rawDataToBusiness(
                    rawData
                )
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

            var i = 0
            rawData
                .textData
                .forEach { data ->
                    var timetable =
                        Timetable()
                    var courseTeacher =
                        CourseTeacher()

                    setCommonData(data, timetable, courseTeacher)

                    timetable.courses = getCourseList(tableList[i].data)
                    courseTeacher.courses = getFacultyList(instructorList[i].data)

                    timetableList.add(timetable)
                    teacherList.add(courseTeacher)

                    i += 1
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
        var courseDetails: Triple<String, String, String>

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

                        courseDetails = populateCourseDetails(cellText)

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

                        val acr = courseDetails.first.trim()
                        courseList.add(
                            Course(
                                label = Label(acr = acr),
                                events = listOf(
                                    RecurrentEvent(
                                        title = null,
                                        description = "${getDescription(courseDetails.third.trim())}$acr",
                                        category = EventCategory.LECTURE.value,
                                        location = listOf(courseDetails.second.trim()),
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

    private fun getFacultyList(data: Array<Array<Cell>>): List<Faculty> {
        val facultyList = mutableListOf<Faculty>()
        var leftFaculty = Faculty()
        var rightFaculty = Faculty()

        for (i in 0 until data.count()) {
            val cells = data[i]

            val leftCourseText = cells[0].text
            val leftTeacherText = cells[1].text
            leftFaculty = populateFaculty(leftCourseText, leftTeacherText, leftFaculty, facultyList)

            if (cells.size > 2) {
                val rightCourseText = cells[2].text
                val rightTeacherText = cells[3].text
                rightFaculty = populateFaculty(rightCourseText, rightTeacherText, rightFaculty, facultyList)
            }
        }

        if (leftFaculty.label !== null) facultyList.add(leftFaculty)
        if (rightFaculty.label !== null) facultyList.add(rightFaculty)

        return facultyList
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

    private fun populateCourseDetails(text: String): Triple<String, String, String> {
        val firstIndex = text.indexOf('(')

        return Triple(
            text.substring(0, text.indexOf('[')),
            text.substring(text.lastIndexOf(')') + 1),
            text.substring(firstIndex + 1, text.indexOf(')', firstIndex))
        )
    }

    private fun getDescription(classType: String) = when (classType) {
        "T" -> "Aulas Teóricas de "
        "P" -> "Aulas Práticas de "
        "L" -> "Aulas Laboratório de "
        "T/P" -> "Aulas Teórico-práticas de "
        else -> ""
    }

    private fun populateFaculty(
        courseText: String,
        teacherText: String,
        faculty: Faculty,
        facultyList: MutableList<Faculty>
    ): Faculty {
        var f = faculty

        if (courseText.isNotEmpty()) {
            if (f.label !== null) {
                facultyList.add(f)
            }

            val courseDetails = populateCourseDetails(courseText)
            f = Faculty(
                label = Label(acr = courseDetails.first.trim())
            )

            f.teachers = mutableListOf(
                Teacher(
                    teacherText
                )
            )
        } else {
            if (!teacherText.isEmpty()) {
                val teacherList = f.teachers.toMutableList()
                teacherList.add(
                    Teacher(
                        teacherText
                    )
                )

                f.teachers = teacherList
            } else {
                if (f.label !== null) {
                    facultyList.add(f)
                    f = Faculty()
                }
            }
        }

        return f
    }
}
