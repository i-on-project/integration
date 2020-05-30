package org.ionproject.integration.model.internal.timetable

data class Event(
    var title: String = "",
    var description: String = "",
    val type: String,
    var location: List<String>,
    val beginTime: String,
    val endTime: String,
    val duration: String,
    val weekday: List<String>
)
