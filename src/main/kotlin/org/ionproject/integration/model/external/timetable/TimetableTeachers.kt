package org.ionproject.integration.model.external.timetable

data class TimetableTeachers(
    var timetable: List<Timetable> = listOf(),
    var teachers: List<CourseTeacher> = listOf()
)
