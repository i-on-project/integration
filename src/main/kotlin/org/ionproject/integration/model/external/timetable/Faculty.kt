package org.ionproject.integration.model.external.timetable

data class Faculty(
    var course: String = "",
    var courseType: String = "",
    var teachers: List<Teacher> = listOf()
)
