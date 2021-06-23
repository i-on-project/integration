package org.ionproject.integration.domain.timetable

abstract class Event(
    var title: String?,
    var description: String = "",
    val category: EventCategory,
    var location: List<String>
)
