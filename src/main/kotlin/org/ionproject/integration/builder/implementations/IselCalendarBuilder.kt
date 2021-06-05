package org.ionproject.integration.builder.implementations

import org.ionproject.integration.model.external.calendar.Calendar
import org.ionproject.integration.model.external.calendar.Terms
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.internal.calendar.isel.RawCalendarData
import org.ionproject.integration.utils.DateFormat
import java.util.Date

class IselCalendarBuilder {
    fun buildIselCalendar(rawCalendarData: RawCalendarData): Calendar =
        Calendar(
            creationDateTime = rawCalendarData.creationDate,
            retrievalDateTime = DateFormat.format(Date()),
            School(), // todo
            buildTerms(rawCalendarData)
        )

    // TODO
    private fun buildTerms(rawCalendarData: RawCalendarData):List<Terms> {
        return emptyList()
    }
}
