package org.ionproject.integration.model.external.calendar

import org.ionproject.integration.domain.dto.SchoolDto
import org.ionproject.integration.utils.DateUtils

data class AcademicCalendarDto(
    val creationDateTime: String,
    val retrievalDateTime: String,
    val school: SchoolDto,
    val language: String,
    val terms: List<TermDto>
) {
    companion object {
        fun from(academicCalendar: AcademicCalendar): AcademicCalendarDto {
            return AcademicCalendarDto(
                academicCalendar.creationDateTime,
                academicCalendar.retrievalDateTime,
                SchoolDto(academicCalendar.school.name, academicCalendar.school.acr),
                academicCalendar.language.value,
                TermDto.from(academicCalendar.terms)
            )
        }
    }
}

data class TermDto(
    val calendarTerm: String,
    val interruptions: List<EventDto>,
    val evaluations: List<EvaluationDto>,
    val details: List<DetailDto>,
    val otherEvents: List<EventDto>
) {
    companion object {
        fun from(terms: List<Term>): List<TermDto> = terms.map {
            TermDto(
                it.calendarTerm,
                EventDto.from(it.interruptions),
                EvaluationDto.from(it.evaluations),
                DetailDto.from(it.details),
                EventDto.from(it.otherEvents)
            )
        }
    }
}

data class EventDto(
    val name: String,
    val startDate: String,
    val endDate: String
) {
    companion object {
        fun from(events: List<Event>): List<EventDto> = events.map {
            EventDto(
                it.name,
                DateUtils.formatToCalendarDate(it.startDate),
                DateUtils.formatToCalendarDate(it.endDate)
            )
        }
    }
}

data class EvaluationDto(
    val name: String,
    val startDate: String,
    val endDate: String,
    val duringLectures: Boolean
) {
    companion object {
        fun from(evaluations: List<Evaluation>): List<EvaluationDto> = evaluations.map {
            EvaluationDto(
                it.name,
                DateUtils.formatToCalendarDate(it.startDate),
                DateUtils.formatToCalendarDate(it.endDate),
                it.duringLectures
            )
        }
    }
}

data class DetailDto(
    val name: String,
    val curricularTerm: List<IdDto>,
    val startDate: String,
    val endDate: String,
) {
    companion object {
        fun from(details: List<Detail>): List<DetailDto> = details.map {
            DetailDto(
                it.name,
                it.curricularTerm.map { id -> IdDto(id) },
                DateUtils.formatToCalendarDate(it.startDate),
                DateUtils.formatToCalendarDate(it.endDate)
            )
        }
    }
}

data class IdDto(
    val id: Int
)
