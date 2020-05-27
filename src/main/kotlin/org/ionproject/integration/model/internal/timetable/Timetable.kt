package org.ionproject.integration.model.internal.timetable

data class Timetable(
    var school: String = "",
    var programme: String = "",
    var calendarTerm: String = "",
    var classSection: String = "",
    var courses: List<Course> = listOf()
)
