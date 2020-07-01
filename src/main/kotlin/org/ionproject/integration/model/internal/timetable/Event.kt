package org.ionproject.integration.model.internal.timetable

data class Event(
    @Transient var title: String = "",
    var description: String = "",
    val category: String,
    var location: List<String>,
    val beginTime: String,
    val duration: String,
    val weekday: List<String>
)
