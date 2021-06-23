package org.ionproject.integration.domain.timetable

data class TimetableTeachers(
    var timetable: List<Timetable> = listOf(),
    var teachers: List<CourseTeacher> = listOf()
)
