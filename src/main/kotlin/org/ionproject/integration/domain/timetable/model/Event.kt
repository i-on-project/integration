package org.ionproject.integration.domain.timetable.model

abstract class Event(
    var title: String?,
    val category: EventCategory,
    var location: List<String>
)
