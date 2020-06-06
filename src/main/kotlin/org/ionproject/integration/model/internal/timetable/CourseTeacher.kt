package org.ionproject.integration.model.internal.timetable

data class CourseTeacher(
    var school: School = School(),
    var programme: Programme = Programme(),
    var calendarTerm: String = "",
    var calendarSection: String = "",
    var faculty: List<Faculty> = listOf()
)