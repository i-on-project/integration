package org.ionproject.integration.model.external.calendar

import org.ionproject.integration.model.external.timetable.SchoolDto
import org.ionproject.integration.utils.DateUtils

data class CalendarDto(
    val creationDateTime: String,
    val retrievalDateTime: String,
    val school: SchoolDto,
    val language: String,
    val terms: List<TermDto>
) {
    companion object {
        fun from(academicCalendar: Calendar): CalendarDto {
            return CalendarDto(
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
        fun from(terms: List<Term>): List<TermDto> {
            val result = mutableListOf<TermDto>()
            for (term in terms) {
                result.add(
                    TermDto(
                        term.calendarTerm,
                        EventDto.from(term.interruptions),
                        EvaluationDto.from(term.evaluations),
                        DetailDto.from(term.details),
                        EventDto.from(term.otherEvents)
                    )
                )
            }
            return result.toList()
        }
    }
}

data class EventDto(
    val name: String,
    val startDate: String,
    val endDate: String
) {
    companion object {
        fun from(events: List<Event>): List<EventDto> {
            val result = mutableListOf<EventDto>()
            for (event in events) {
                result.add(
                    EventDto(
                        event.name,
                        DateUtils.getDateRepresentation(event.startDate),
                        DateUtils.getDateRepresentation(event.endDate)
                    )
                )
            }
            return result
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
        fun from(evaluations: List<Evaluation>): List<EvaluationDto> {
            val result = mutableListOf<EvaluationDto>()
            for (evaluation in evaluations) {
                result.add(
                    EvaluationDto(
                        evaluation.name,
                        DateUtils.getDateRepresentation(evaluation.startDate),
                        DateUtils.getDateRepresentation(evaluation.endDate),
                        evaluation.duringLectures
                    )
                )
            }
            return result
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
        fun from(details: List<Detail>): List<DetailDto> {
            val result = mutableListOf<DetailDto>()
            for (detail in details) {
                result.add(
                    DetailDto(
                        detail.name,
                        detail.curricularTerm.map { IdDto(it) },
                        DateUtils.getDateRepresentation(detail.startDate),
                        DateUtils.getDateRepresentation(detail.endDate)
                    )
                )
            }
            return result
        }
    }
}

data class IdDto(
    val id: Int
)
