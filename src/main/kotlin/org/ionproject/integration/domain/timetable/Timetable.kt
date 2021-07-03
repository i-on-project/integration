package org.ionproject.integration.domain.timetable

import org.ionproject.integration.domain.timetable.model.Course
import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.School

data class Timetable(
    var creationDateTime: String = "",
    var retrievalDateTime: String = "",
    var school: School = School(),
    var programme: Programme = Programme(),
    var calendarTerm: String = "",
    var calendarSection: String = "",
    var courses: List<Course> = listOf()
)
