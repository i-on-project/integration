package org.ionproject.integration.domain.timetable.dto

data class RawTimetableData(
    val scheduleData: String,
    val textData: List<String>,
    val instructorData: String,
    val creationDate: String
)
