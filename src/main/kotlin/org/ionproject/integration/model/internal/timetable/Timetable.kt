package org.ionproject.integration.model.internal.timetable

data class Timetable(
    var school: School = School(),
    var programme: Programme = Programme(),
    var calendarTerm: String = "",
    var calendarSection: String = "",
    var courses: List<Course> = listOf()
)
