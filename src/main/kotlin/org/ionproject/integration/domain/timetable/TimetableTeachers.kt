package org.ionproject.integration.domain.timetable

import org.ionproject.integration.domain.timetable.model.CourseTeacher

data class TimetableTeachers(
    var timetable: List<Timetable> = listOf(),
    var teachers: List<CourseTeacher> = listOf()
)
