package org.ionproject.integration.model.external.calendar

import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.internal.calendar.isel.RawCalendarData
import org.ionproject.integration.utils.DateFormat
import org.ionproject.integration.utils.RegexUtils
import java.util.Date

data class Calendar(
    var creationDateTime: String = "",
    var retrievalDateTime: String = "",
    var school: School = School(),
    var terms: List<Terms> = listOf()
) {
    // Business Logic Validation
    init {
    }

    companion object {
        private const val EVENT_REGEX = """(?<=text":")[^"{}]+"""

        fun from(rawCalendarData: RawCalendarData): Calendar =
            Calendar(
                creationDateTime = rawCalendarData.creationDate,
                retrievalDateTime = DateFormat.format(Date()),
                School(), // todo
                buildTerms(rawCalendarData)
            )

        // TODO 
        private fun buildTerms(rawCalendarData: RawCalendarData): List<Terms> {
            val xixas = parseTables(rawCalendarData)
            return emptyList()
        }

        private fun parseTables(rawCalendarData: RawCalendarData): List<String> =
            RegexUtils.findMatches(EVENT_REGEX, rawCalendarData.table)
    }
}

data class Terms(
    val calendarTerm: String = "",
    val interruptions: List<Events>,
    val evaluations: List<Evaluations>,
    val details: List<Details>,
    val otherEvents: List<Events>
)

data class Events(
    val name: String = "",
    val startDate: Date,
    val endDate: Date
)

data class Evaluations(
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val duringLectures: Boolean
)

data class Details(
    val name: String,
    val curricularTerm: List<Int>,
    val startDate: Date,
    val endDate: Date
)
