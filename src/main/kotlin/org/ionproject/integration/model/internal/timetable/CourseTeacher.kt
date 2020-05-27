package org.ionproject.integration.model.internal.timetable

data class CourseTeacher(
    var school: String = "",
    var programme: String = "",
    var calendarTerm: String = "",
    var classSection: String = "",
    var faculty: List<Faculty> = listOf()
)
