package org.ionproject.integration.model.external.timetable

data class Event(
    @Transient var title: String = "",
    var description: String = "",
    val category: Int,
    var location: List<String>,
    val startDate: String,
    val endDate: String,
    val weekday: List<String>
)
