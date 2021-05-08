package org.ionproject.integration.model.external.timetable

data class Timetable(
    var creationDateTime: String = "",
    var retrievalDateTime: String = "",
    var school: School = School(),
    var programme: Programme = Programme(),
    var calendarTerm: String = "",
    var calendarSection: String = "",
    var courses: List<Course> = listOf()
)
