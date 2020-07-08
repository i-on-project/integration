package org.ionproject.integration.model.external.timetable

import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.Timetable

data class TimetableTeachers(
    var timetable: List<Timetable> = listOf(),
    var teachers: List<CourseTeacher> = listOf()
)
