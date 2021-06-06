package org.ionproject.integration.model.external.calendar

import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.internal.calendar.isel.RawCalendarData
import org.ionproject.integration.utils.DateFormat
import org.ionproject.integration.utils.DateUtils
import org.ionproject.integration.utils.RegexUtils
import java.util.Date

data class Calendar(
    var creationDateTime: String = "",
    var retrievalDateTime: String = "",
    var school: School = School(),
    var terms: List<Term> = listOf()
) {
    companion object {
        private const val EVENT_REGEX = """(?<=text":")[^"{}]+"""
        private const val CALENDAR_TERM_REGEX = """(?<=\sCalendário\sEscolar\s).+?(?=\r|\R)"""
        private const val TABLE_DELIMITER = "extraction"

        fun from(rawCalendarData: RawCalendarData): Calendar =
            Calendar(
                creationDateTime = rawCalendarData.creationDate,
                retrievalDateTime = DateFormat.format(Date()),
                School(), // todo
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

        private fun buildTerm(events: List<String>, text: String, term: CalendarTerm): Term {
            val interruptions = mutableListOf<Event>()
            val evaluations = mutableListOf<Evaluation>()
            val details = mutableListOf<Detail>()
            val otherEvents = mutableListOf<Event>()

            for (index in events.indices step 2) {
                val dateRange = DateUtils.getDateRange(events[index + 1])

                when (getEventType(events[index])) {
                    EventType.EVALUATION -> {
                        evaluations.add(
                            Evaluation(
                                events[index],
                                dateRange[0],
                                dateRange[1],
                                false
                            )
                        )
                    }
                    EventType.INTERRUPTION -> {
                        interruptions.add(
                            Event(
                                events[index],
                                dateRange[0],
                                dateRange[1]
                            )
                        )
                    }
                    EventType.DETAILS -> {
                        details.add(
                            Detail(
                                events[index],
                                listOf<Int>(),
                                dateRange[0],
                                dateRange[1]
                            )
                        )
                    }
                    EventType.OTHER -> {
                        otherEvents.add(
                            Event(
                                events[index],
                                dateRange[0],
                                dateRange[1]
                            )
                        )
                    }
                }
            }

            return Term(
                RegexUtils.findMatches(CALENDAR_TERM_REGEX, text).first().trim().plus("-${getTermNumber(term)}"),
                interruptions.toList(),
                evaluations.toList(),
                details.toList(),
                otherEvents.toList()
            )
        }

        private fun getTermNumber(term: CalendarTerm) =
            when (term) {
                CalendarTerm.WINTER -> "1"
                CalendarTerm.SUMMER -> "2"
            }

        private fun getEventType(event: String): EventType {
            return when {
                event.contains("Interrupção", true) -> EventType.INTERRUPTION
                event.contains("Exames", true) -> EventType.EVALUATION
                event.contains("Turmas", true) -> EventType.DETAILS
                else -> EventType.OTHER
            }
        }

        private fun isDuringLectures(eventDate: Date, lectureBeginDate: Date, lectureEndDate: Date): Boolean {
            return (eventDate.after(lectureBeginDate) && eventDate.before(lectureEndDate))
        }
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
    val startDate: Date,
    val endDate: Date
)

data class Evaluation(
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val duringLectures: Boolean
)

data class Detail(
    val name: String,
    val curricularTerm: List<Int>,
    val startDate: Date,
    val endDate: Date
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
