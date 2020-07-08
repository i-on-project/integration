package org.ionproject.integration.model.external.timetable

import org.ionproject.integration.model.external.timetable.Course
import org.ionproject.integration.model.external.timetable.Programme
import org.ionproject.integration.model.external.timetable.School

data class Timetable(
    var school: School = School(),
    var programme: Programme = Programme(),
    var calendarTerm: String = "",
    var calendarSection: String = "",
    var language: String = "",
    var courses: List<Course> = listOf()
)
