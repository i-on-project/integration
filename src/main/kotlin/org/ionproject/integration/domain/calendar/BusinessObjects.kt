package org.ionproject.integration.domain.calendar

import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.Language
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.infrastructure.DateUtils
import org.ionproject.integration.infrastructure.text.RegexUtils
import java.time.LocalDate
import java.time.ZonedDateTime

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
        private const val PT_EVALUATION_REGEX = "\\b(?:Período de exames|Exames de época especial)\\b"
        private const val PT_LECTURES_REGEX = "\\b(?:Turmas)\\b"
        private const val PT_LECTURES_START_REGEX = "\\b(?:Início das aulas)\\b"
        private const val PT_LECTURES_END_REGEX = "\\b(?:Fim das aulas)\\b"
        private const val PT_LECTURES_ALL_SECTIONS_NAME = "Todas as turmas"

        fun from(rawCalendarData: RawCalendarData, jobInstitution: InstitutionModel): AcademicCalendar =
            AcademicCalendar(
                creationDateTime = rawCalendarData.creationDate,
                retrievalDateTime = DateUtils.formatToISO8601(ZonedDateTime.now()),
                School(
                    jobInstitution.name,
                    jobInstitution.acronym
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
            val otherEvents = mutableListOf<Event>()
            val (descriptions, dates) = events.withIndex().partition { it.index % 2 == 0 }
            val lectures = getLectures(descriptions, dates)

            descriptions.forEachIndexed { index, _ ->
                val intervalDate = DateUtils.getDateRange(dates[index].value)

                when (getEventType(descriptions[index].value)) {
                    EventType.EVALUATION -> {
                        evaluations.add(
                            Evaluation(
                                descriptions[index].value,
                                intervalDate.from,
                                intervalDate.to,
                                false
                            )
                        )
                    }
                    EventType.INTERRUPTION -> {
                        interruptions.add(
                            Event(
                                descriptions[index].value,
                                intervalDate.from,
                                intervalDate.to,
                            )
                        )
                    }
                    EventType.LECTURES -> {
                        // Do nothing - they were processed on top
                    }
                    EventType.OTHER -> {
                        otherEvents.add(
                            Event(
                                descriptions[index].value,
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
                lectures,
                otherEvents
            )
        }

        private fun getLectures(
            descriptions: List<IndexedValue<String>>,
            dates: List<IndexedValue<String>>
        ): List<Lectures> {
            val lectures = mutableListOf<Lectures>()

            val numberOfLecturesEvents =
                descriptions.filter { it.value.contains(PT_LECTURES_START_REGEX.toRegex(RegexOption.IGNORE_CASE)) }
                    .count()

            /* if it's a single event then it's for all academic terms*/
            if (numberOfLecturesEvents == 1) {
                lectures.add(
                    Lectures(
                        PT_LECTURES_ALL_SECTIONS_NAME,
                        listOf(1, 2, 3, 4, 5, 6),
                        findFirstDateRangeFromEvent(descriptions, dates, PT_LECTURES_START_REGEX).from,
                        findFirstDateRangeFromEvent(descriptions, dates, PT_LECTURES_END_REGEX).from,
                    )
                )
                // if there are multiple events it's necessary to split per each section
            } else {
                // TODO: when there's different dates for different sections
            }

            return lectures
        }

        private fun findFirstDateRangeFromEvent(
            descriptions: List<IndexedValue<String>>,
            dates: List<IndexedValue<String>>,
            regex: String
        ) =
            DateUtils.getDateRange(
                dates[
                    descriptions.indexOf(
                        descriptions.find {
                            it.value.contains(
                                regex.toRegex(
                                    RegexOption.IGNORE_CASE
                                )
                            )
                        }
                    )
                ].value
            )

        private fun getTermNumber(term: CalendarTerm) =
            when (term) {
                CalendarTerm.WINTER -> "1"
                CalendarTerm.SUMMER -> "2"
            }

        private fun getEventType(event: String): EventType {
            return when {
                event.contains(PT_INTERRUPTION_REGEX.toRegex(RegexOption.IGNORE_CASE)) -> EventType.INTERRUPTION
                event.contains(PT_EVALUATION_REGEX.toRegex(RegexOption.IGNORE_CASE)) -> EventType.EVALUATION
                event.contains(PT_LECTURES_REGEX.toRegex(RegexOption.IGNORE_CASE)) -> EventType.LECTURES
                event.contains(PT_LECTURES_START_REGEX.toRegex(RegexOption.IGNORE_CASE)) -> EventType.LECTURES
                event.contains(PT_LECTURES_END_REGEX.toRegex(RegexOption.IGNORE_CASE)) -> EventType.LECTURES
                else -> EventType.OTHER
            }
        }

        private fun isDuringLectures(
            eventDate: LocalDate,
            lectureBeginDate: LocalDate,
            lectureEndDate: LocalDate
        ): Boolean =
            eventDate in lectureBeginDate..lectureEndDate
    }
}

data class Term(
    val calendarTerm: String = "",
    val interruptions: List<Event>,
    val evaluations: List<Evaluation>,
    val lectures: List<Lectures>,
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

data class Lectures(
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
    LECTURES,
    OTHER
}
