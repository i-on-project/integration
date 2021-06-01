package org.ionproject.integration.model.internal.calendar.isel

data class RawCalendarData(
    val calendarData: String,
    val textData: List<String>,
    val summerSemester: String,
    val winterSemester: String,
    val creationDate: String
)
