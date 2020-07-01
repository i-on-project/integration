package org.ionproject.integration.builder.implementations

import com.squareup.moshi.Types
import java.time.Duration
import java.time.LocalTime
import org.ionproject.integration.builder.exceptions.TimetableTeachersBuilderException
import org.ionproject.integration.builder.interfaces.ITimetableTeachersBuilder
import org.ionproject.integration.model.internal.tabula.Cell
import org.ionproject.integration.model.internal.tabula.Table
import org.ionproject.integration.model.internal.timetable.Course
import org.ionproject.integration.model.internal.timetable.CourseTeacher
import org.ionproject.integration.model.internal.timetable.Event
import org.ionproject.integration.model.internal.timetable.EventCategory
import org.ionproject.integration.model.internal.timetable.Faculty
import org.ionproject.integration.model.internal.timetable.Label
import org.ionproject.integration.model.internal.timetable.Programme
import org.ionproject.integration.model.internal.timetable.School
import org.ionproject.integration.model.internal.timetable.Teacher
import org.ionproject.integration.model.internal.timetable.Timetable
import org.ionproject.integration.model.internal.timetable.TimetableTeachers
import org.ionproject.integration.model.internal.timetable.Weekdays
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.RegexUtils
import org.ionproject.integration.utils.Try

class IselTimetableTeachersBuilder() : ITimetableTeachersBuilder<RawData> {
    companion object {
        private val SCHOOL_REGEX = "\\A.*"
        private val PROGRAMME_REGEX = "^(Licenciatura|Mestrado).*$"
        private val CLASS_SECTION_REGEX = "\\bTurma\\b: [LM][A-Z+]+\\d{1,2}[DN]"
        private val CALENDAR_TERM_REGEX = "\\bAno Letivo\\b: .+?(\\r|\\R)"
        private val TIME_SLOT_REGEX = "([8-9]|1[0-9]|2[0-3]).(0|3)0"
        private val HEIGHT_ONE_HALF_HOUR_THRESHOLD = 47
        private val HEIGHT_HALF_HOUR_THRESHOLD = 17
    }

    private var iselTimetableTeachers = Try.of { TimetableTeachers() }

    override fun setTimetable(rawData: RawData) {
        iselTimetableTeachers
            .map { timetableTeachers -> if (timetableTeachers.timetable.count() == 0 || timetableTeachers.teachers.count() == 0) rawDataToBusiness(rawData) }
    }

    override fun setTeachers(rawData: RawData) {
        setTimetable(rawData)
    }

    fun getTimetableTeachers(): Try<TimetableTeachers> {
        return iselTimetableTeachers.map { it.copy() }
    }

    private fun rawDataToBusiness(rawData: RawData) {
        JsonUtils.fromJson<List<Table>>(rawData.jsonData, Types.newParameterizedType(List::class.java, Table::class.java))
            .map { mapTablesToBusiness(rawData, it) }
    }

    private fun mapTablesToBusiness(rawData: RawData, tableList: List<Table>) {
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
                    courseTeacher.faculty = getFacultyList(tableList[i + 1].data)

                    timetableList.add(timetable)
                    teacherList.add(courseTeacher)

                    i += 2
                }

            TimetableTeachers(timetableList, teacherList)
        }
    }

    private fun setCommonData(data: String, timetable: Timetable, courseTeacher: CourseTeacher) {
        val school = RegexUtils.findMatches(SCHOOL_REGEX, data)[0].trimEnd()
        val programme = RegexUtils.findMatches(PROGRAMME_REGEX, data, RegexOption.MULTILINE)[0].trimEnd()
        val calendarTerm = RegexUtils.findMatches(CALENDAR_TERM_REGEX, data, RegexOption.MULTILINE)[0].replace("Ano Letivo:", "").trim()
        val classSection = RegexUtils.findMatches(CLASS_SECTION_REGEX, data, RegexOption.MULTILINE)[0].replace("Turma:", "").trim()

        timetable.school = School(name = school)
        timetable.programme = Programme(name = programme)
        timetable.calendarTerm = calendarTerm
        timetable.calendarSection = classSection
        timetable.language = "pt-PT"

        courseTeacher.school = School(name = school)
        courseTeacher.programme = Programme(name = programme)
        courseTeacher.calendarTerm = calendarTerm
        courseTeacher.calendarSection = classSection
        courseTeacher.language = "pt-PT"
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

                if (cell.text.isNullOrEmpty()) continue

                val matches = RegexUtils.findMatches(TIME_SLOT_REGEX, cells[j].text)
                if (matches.count() != 0) {
                    beginTime = getBeginTime(matches)
                    continue
                }

                cell.text.split('\r')
                    .forEach {
                        val cellText = if (it.contains('[')) it else cell.text

                        courseDetails = populateCourseDetails(cellText)

                        var duration: Duration = if (cell.height > HEIGHT_ONE_HALF_HOUR_THRESHOLD) {
                            Duration.ofHours(3)
                        } else if (cell.height > HEIGHT_HALF_HOUR_THRESHOLD) {
                            Duration.ofHours(1).plusMinutes(30)
                        } else {
                            Duration.ofMinutes(30)
                        }

                        if (!weekdays.contains(cell.left)) throw TimetableTeachersBuilderException("Can't find weekday")

                        val acr = courseDetails.first.trim()
                        courseList.add(
                            Course(
                                label = Label(acr = acr),
                                events = listOf(
                                    Event(description = "${getDescription(courseDetails.third.trim())}$acr",
                                        category = EventCategory.CLASS.toString(),
                                        location = listOf(courseDetails.second.trim()),
                                        beginTime = beginTime.toString(),
                                        duration = duration.toString(),
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
            val rightCourseText = cells[2].text
            val rightTeacherText = cells[3].text

            leftFaculty = populateFaculty(leftCourseText, leftTeacherText, leftFaculty, facultyList)
            rightFaculty = populateFaculty(rightCourseText, rightTeacherText, rightFaculty, facultyList)
        }

        if (!leftFaculty.course.isNullOrEmpty()) facultyList.add(leftFaculty)
        if (!rightFaculty.course.isNullOrEmpty()) facultyList.add(rightFaculty)

        return facultyList
    }

    private fun populateWeekdays(cells: Array<Cell>, weekdays: MutableMap<Double, String>) {
        for (i in 0 until cells.count()) {
            if (cells[i].width == 0.0 || cells[i].height == 0.0 || cells[i].text.isNullOrEmpty()) continue
            weekdays[cells[i].left] = Weekdays.values().first { it.toPortuguese() == cells[i].text.toUpperCase() }.toShortString()
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

    private fun populateFaculty(courseText: String, teacherText: String, faculty: Faculty, facultyList: MutableList<Faculty>): Faculty {
        var f = faculty

        if (!courseText.isNullOrEmpty()) {
            if (!f.course.isNullOrEmpty()) {
                facultyList.add(f)
            }

            val courseDetails = populateCourseDetails(courseText)
            f = Faculty(
                course = courseDetails.first.trim(),
                courseType = courseDetails.third
            )

            f.teachers = mutableListOf(
                Teacher(
                    teacherText
                )
            )
        } else {
            if (!teacherText.isNullOrEmpty()) {
                var teacherList = f.teachers.toMutableList()
                teacherList.add(
                    Teacher(
                        teacherText
                    )
                )

                f.teachers = teacherList
            } else {
                if (!f.course.isNullOrEmpty()) {
                    facultyList.add(f)
                    f = Faculty()
                }
            }
        }

        return f
    }
}
