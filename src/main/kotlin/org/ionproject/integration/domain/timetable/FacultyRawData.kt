package org.ionproject.integration.builder.implementations

import org.ionproject.integration.domain.timetable.ClassDetail
import org.ionproject.integration.domain.timetable.Instructor

data class FacultyRawData(val courseText: String, val instructorText: String)

// TODO change this, the DTO is defined elsewhere
data class FacultyDTO(val classDetail: ClassDetail, val instructor: Instructor) {
    fun isValid(): Boolean = instructor.name.isNotBlank()
}

fun FacultyRawData.toDto(): FacultyDTO {
    val classDetail = ClassDetail.from(courseText)
    val instructor = Instructor(instructorText)
    return FacultyDTO(classDetail, instructor)
}
