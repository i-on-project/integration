package org.ionproject.integration.builder.implementations

import com.squareup.moshi.Types
import java.time.Duration
import java.time.LocalDateTime
import org.ionproject.integration.builder.interfaces.TimetableTeachersBuilder
import org.ionproject.integration.model.internal.tabula.Cell
import org.ionproject.integration.model.internal.tabula.Table
import org.ionproject.integration.model.internal.timetable.Course
import org.ionproject.integration.model.internal.timetable.CourseTeacher
import org.ionproject.integration.model.internal.timetable.Faculty
import org.ionproject.integration.model.internal.timetable.Teacher
import org.ionproject.integration.model.internal.timetable.Timetable
import org.ionproject.integration.model.internal.timetable.TimetableTeachers
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.RegexUtils

class IselTimetableTeachersBuilder() : TimetableTeachersBuilder<RawData> {
    val SCHOOL_REGEX = "\\A.*"
    val PROGRAMME_REGEX = "^(Licenciatura|Mestrado).*$"
    val CLASS_SECTION_REGEX = "\\bTurma\\b: [LM][A-Z+]\\d{2}[DN]"
    val CALENDAR_TERM_REGEX = "\\bAno Letivo\\b: \\d{4}/\\d{2}-\\b(Verão|Inverno)\\b"
    val TIME_SLOT_REGEX = "([8-9]|1[0-9]|2[0-3]).(0|3)0"
    val HEIGHT_THRESHOLD = 47

    private lateinit var iselTimetableTeachers: TimetableTeachers

    init {
        reset()
    }

    override fun reset() {
        iselTimetableTeachers =
            TimetableTeachers()
    }

    override fun setTimetable(rawData: RawData) {
        if (iselTimetableTeachers.timetable.count() != 0 && iselTimetableTeachers.teachers.count() != 0)
            return

        rawDataToBusiness(rawData)
    }

    override fun setTeachers(rawData: RawData) {
        setTimetable(rawData)
    }

    fun getTimetableTeachers(): TimetableTeachers {
        val result = this.iselTimetableTeachers.copy()
        reset()
        return result
    }

    private fun rawDataToBusiness(rawData: RawData) {
        JsonUtils.fromJson<List<Table>>(rawData.jsonData, Types.newParameterizedType(List::class.java, Table::class.java))
            .map {
                var timetableList = mutableListOf<Timetable>()
                var teacherList = mutableListOf<CourseTeacher>()

                var i = 0
                rawData
                    .textData
                    .forEach { data ->
                        var timetable =
                            Timetable()
                        var courseTeacher =
                            CourseTeacher()

                        setCommonData(data, timetable, courseTeacher)

                        timetable.courses = getCourseList(it[i].data)
                        courseTeacher.faculty = getFacultyList(it[i + 1].data)

                        timetableList.add(timetable)
                        teacherList.add(courseTeacher)

                        i += 2
                    }

                iselTimetableTeachers.timetable = timetableList
                iselTimetableTeachers.teachers = teacherList
            }
    }

    private fun setCommonData(data: String, timetable: Timetable, courseTeacher: CourseTeacher) {
        val school = RegexUtils.findMatches(SCHOOL_REGEX, data)[0].trimEnd()
        val programme = RegexUtils.findMatches(PROGRAMME_REGEX, data, RegexOption.MULTILINE)[0].trimEnd()
        val calendarTerm = RegexUtils.findMatches(CALENDAR_TERM_REGEX, data, RegexOption.MULTILINE)[0].replace("Ano Letivo:", "").trim()
        val classSection = RegexUtils.findMatches(CLASS_SECTION_REGEX, data, RegexOption.MULTILINE)[0].replace("Turma:", "").trim()

        timetable.school = school
        timetable.programme = programme
        timetable.calendarTerm = calendarTerm
        timetable.classSection = classSection

        courseTeacher.school = school
        courseTeacher.programme = programme
        courseTeacher.calendarTerm = calendarTerm
        courseTeacher.classSection = classSection
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

            var beginTime = LocalDateTime.now()

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
                        courseDetails = populateCourseDetails(it)

                        var duration: Duration = if (cell.height > HEIGHT_THRESHOLD) {
                            Duration.ofHours(3)
                        } else {
                            Duration.ofHours(1).plusMinutes(30)
                        }

                        courseList.add(
                            Course(
                                acronym = courseDetails.first.trim(),
                                type = courseDetails.second,
                                room = courseDetails.third,
                                begin_time = beginTime.toString(),
                                end_time = beginTime.plusSeconds(duration.toSeconds()).toString(),
                                duration = duration.toString(),
                                weekday = weekdays.getOrDefault(cell.left, "")
                            )
                        )
                    }
            }
        }

        return courseList
    }

    private fun getFacultyList(data: Array<Array<Cell>>): List<Faculty> {
        var facultyList = mutableListOf<Faculty>()
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
            weekdays[cells[i].left] = cells[i].text
        }
    }

    private fun getBeginTime(matches: List<String>): LocalDateTime? {
        val today = LocalDateTime.now()
        val time = matches[0]
            .split('.')

        return LocalDateTime
            .of(today.year, today.monthValue, today.dayOfMonth, time[0].toInt(), time[1].toInt())
    }

    private fun populateCourseDetails(text: String): Triple<String, String, String> {
        val firstIndex = text.indexOf('(')
        val secondIndex = text.indexOf(')') + 1

        return Triple(
            text.substring(0, firstIndex),
            text.substring(firstIndex, secondIndex),
            text.substring(secondIndex)
        )
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
                course_type = courseDetails.second
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
