package org.ionproject.integration.model.external.calendar

import org.ionproject.integration.model.external.timetable.Language
import org.ionproject.integration.model.external.timetable.SchoolDto

data class CalendarDto(
    val creationDateTime: String,
    val retrievalDateTime: String,
    val school: SchoolDto,
    val language: Language,
    val terms: List<TermsDto>
)

data class TermsDto(
    val calendarTerm: String,
    val interruptions: List<EventsDto>,
    val evaluations: List<EvaluationsDto>,
    val details: List<DetailsDto>,
    val otherEvents: List<EventsDto>
)

data class EventsDto(
    val name: String,
    val startDate: String,
    val endDate: String
)

data class EvaluationsDto(
    val name: String,
    val startDate: String,
    val endDate: String,
    val duringLectures: Boolean
)

data class DetailsDto(
    val name: String,
    val curricularTerm: List<Int>,
    val startDate: String,
    val endDate: String,
)
