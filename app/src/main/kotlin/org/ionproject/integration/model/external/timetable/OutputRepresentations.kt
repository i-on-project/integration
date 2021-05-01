package org.ionproject.integration.model.external.timetable

import com.fasterxml.jackson.annotation.JsonInclude

data class TimetableDto(
    val creationDateTime: String,
    val retrievalDateTime: String,
    val school: SchoolDto,
    val programme: ProgrammeDto,
    val calendarTerm: String,
    val classes: List<ClassDto>
)

data class SchoolDto(
    val name: String,
    val acr: String,
)

data class ProgrammeDto(
    val name: String,
    val acr: String,
)

data class ClassDto(
    val acr: String,
    val sections: List<SectionDto>
)

data class SectionDto(
    val section: String,
    val events: List<EventDto>,
    val instructors: List<InstructorDto>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EventDto(
    val category: String,
    val location: List<String>? = null,
    val beginTime: String, // Format HH:MM
    val duration: String, // Format HH:MM
    val weekdays: String
)

data class InstructorDto(
    val name: String,
    val category: String
)
