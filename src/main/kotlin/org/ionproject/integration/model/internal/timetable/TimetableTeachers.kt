package org.ionproject.integration.model.internal.timetable

data class TimetableTeachers(
    var timetable: List<Timetable> = listOf(),
    var teachers: List<CourseTeacher> = listOf()
)
