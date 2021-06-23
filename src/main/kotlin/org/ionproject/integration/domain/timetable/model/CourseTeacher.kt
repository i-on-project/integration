package org.ionproject.integration.domain.timetable.model

import org.ionproject.integration.domain.common.School

data class CourseTeacher(
    var school: School = School(),
    var programme: Programme = Programme(),
    var calendarTerm: String = "",
    var calendarSection: String = "",
    var courses: List<Faculty> = listOf()
)
