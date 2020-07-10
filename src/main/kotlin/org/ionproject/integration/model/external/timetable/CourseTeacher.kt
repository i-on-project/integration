package org.ionproject.integration.model.external.timetable

data class CourseTeacher(
    var school: School = School(),
    var programme: Programme = Programme(),
    var calendarTerm: String = "",
    var calendarSection: String = "",
    var language: String = "",
    var courses: List<Faculty> = listOf()
)
