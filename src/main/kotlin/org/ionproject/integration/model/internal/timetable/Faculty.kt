package org.ionproject.integration.model.internal.timetable

data class Faculty(
    var course: String = "",
    var course_type: String = "",
    var teachers: List<Teacher> = listOf()
)
