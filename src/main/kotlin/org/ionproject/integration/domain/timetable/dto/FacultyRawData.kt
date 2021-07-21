package org.ionproject.integration.domain.timetable.dto

import org.ionproject.integration.domain.timetable.model.ClassDetail
import org.ionproject.integration.domain.timetable.model.Instructor

data class FacultyRawData(val courseText: String, val instructorText: String)

data class FacultyDTO(val classDetail: ClassDetail, val instructor: Instructor) {
    fun isValid(): Boolean = instructor.name.isNotBlank()
}

fun FacultyRawData.toDto(): FacultyDTO {
    val classDetail = ClassDetail.from(courseText)
    val instructor = Instructor(instructorText)
    return FacultyDTO(classDetail, instructor)
}
