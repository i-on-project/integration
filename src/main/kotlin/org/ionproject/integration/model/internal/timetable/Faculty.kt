package org.ionproject.integration.model.internal.timetable

data class Faculty(
    var course: String = "",
    var courseType: String = "",
    var teachers: List<Teacher> = listOf()
)
