package org.ionproject.integration.model.internal.timetable.isel

data class RawData(
    val jsonData: String,
    val textData: List<String>,
    val instructors: String = ""
)

enum class LectureType {
    Theory, Practice, TheoryPractice, Laboratory
}

data class InstructorDto(val name: String, val course: String, val lectureType: String)
