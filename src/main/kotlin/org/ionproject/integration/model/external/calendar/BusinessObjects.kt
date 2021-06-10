package org.ionproject.integration.model.external.calendar

import org.ionproject.integration.model.external.timetable.Language
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.internal.calendar.isel.RawCalendarData
import org.ionproject.integration.utils.DateUtils
import org.ionproject.integration.utils.RegexUtils
import java.time.LocalDate

data class AcademicCalendar(
    val creationDateTime: String = "",
    val retrievalDateTime: String = "",
    val school: School = School(),
    val language: Language = Language.PT,
    val terms: List<Term> = listOf()
) {
    companion object {
        private const val EVENT_REGEX = """(?<=text":")[^"{}]+"""
        private const val CALENDAR_TERM_REGEX = """(?<=\sCalendário\sEscolar\s).+?(?=\r|\R)"""
        private const val TABLE_DELIMITER = "extraction"
        private const val PT_INTERRUPTION_REGEX = "\\b(?:Interrupção|Férias)\\b"
        private const val PT_EVALUATION_REGEX = "\\b(?:Exames|Testes)\\b"
        private const val PT_DETAILS_REGEX = "\\b(?:Turmas)\\b"

        fun from(rawCalendarData: RawCalendarData): AcademicCalendar =
            AcademicCalendar(
                creationDateTime = rawCalendarData.creationDate,
                retrievalDateTime = DateUtils.formatToISO8601(LocalDate.now()),
                School(
                    "Instituto Superior de Engenharia de Lisboa",
                    "ISEL"
                ),
                Language.PT,
                buildTerms(rawCalendarData)
            )

        private fun buildTerms(rawCalendarData: RawCalendarData): List<Term> {
            val termList = mutableListOf<Term>()
            val tables = rawCalendarData.table.split(TABLE_DELIMITER)
            val term1 = parseTables(tables[1])
            val term2 = parseTables(tables[2] + tables[3])

            termList.add(buildTerm(term1, rawCalendarData.textData[0], CalendarTerm.WINTER))
            termList.add(buildTerm(term2, rawCalendarData.textData[0], CalendarTerm.SUMMER))

            return termList
        }

        private fun parseTables(table: String): List<String> =
            RegexUtils.findMatches(EVENT_REGEX, table)

        private fun buildTerm(events: List<String>, pdfRawText: String, term: CalendarTerm): Term {
            val interruptions = mutableListOf<Event>()
            val evaluations = mutableListOf<Evaluation>()
            val details = mutableListOf<Detail>()
            val otherEvents = mutableListOf<Event>()
            val (descriptions, dates) = events.withIndex().partition { it.index % 2 == 0 }

            descriptions.forEachIndexed { index, _ ->
                val intervalDate = DateUtils.getDateRange(dates[index].value)

                when (getEventType(events[index])) {
                    EventType.EVALUATION -> {
                        evaluations.add(
                            Evaluation(
                                events[index],
                                intervalDate.from,
                                intervalDate.to,
                                false
                            )
                        )
                    }
                    EventType.INTERRUPTION -> {
                        interruptions.add(
                            Event(
                                events[index],
                                intervalDate.from,
                                intervalDate.to,
                            )
                        )
                    }
                    EventType.DETAILS -> {
                        details.add(
                            Detail(
                                events[index],
                                listOf(),
                                intervalDate.from,
                                intervalDate.to,
                            )
                        )
                    }
                    EventType.OTHER -> {
                        otherEvents.add(
                            Event(
                                events[index],
                                intervalDate.from,
                                intervalDate.to,
                            )
                        )
                    }
                }
            }

            return Term(
                RegexUtils.findMatches(CALENDAR_TERM_REGEX, pdfRawText)
                    .first()
                    .trim()
                    .replace('/', '-')
                    .plus("-${getTermNumber(term)}"),
                interruptions,
                evaluations,
                details,
                otherEvents
            )
        }

        private fun getTermNumber(term: CalendarTerm) =
            when (term) {
                CalendarTerm.WINTER -> "1"
                CalendarTerm.SUMMER -> "2"
            }

        private fun getEventType(event: String): EventType {
            return when {
                event.contains(PT_INTERRUPTION_REGEX.toRegex(RegexOption.IGNORE_CASE)) -> EventType.INTERRUPTION
                event.contains(PT_EVALUATION_REGEX.toRegex(RegexOption.IGNORE_CASE)) -> EventType.EVALUATION
                event.contains(PT_DETAILS_REGEX.toRegex(RegexOption.IGNORE_CASE)) -> EventType.DETAILS
                else -> EventType.OTHER
            }
        }

        private fun isDuringLectures(eventDate: LocalDate, lectureBeginDate: LocalDate, lectureEndDate: LocalDate): Boolean =
            eventDate in lectureBeginDate..lectureEndDate
    }
}

data class Term(
    val calendarTerm: String = "",
    val interruptions: List<Event>,
    val evaluations: List<Evaluation>,
    val details: List<Detail>,
    val otherEvents: List<Event>
)

data class Event(
    val name: String = "",
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class Evaluation(
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val duringLectures: Boolean
)

data class Detail(
    val name: String,
    val curricularTerm: List<Int>,
    val startDate: LocalDate,
    val endDate: LocalDate
)

enum class CalendarTerm {
    WINTER,
    SUMMER
}

enum class EventType {
    INTERRUPTION,
    EVALUATION,
    DETAILS,
    OTHER
}
