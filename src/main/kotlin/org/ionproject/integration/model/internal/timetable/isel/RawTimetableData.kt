package org.ionproject.integration.model.internal.timetable.isel

data class RawTimetableData(
    val scheduleData: String,
    val textData: List<String>,
    val instructorData: String
)
