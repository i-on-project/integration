package org.ionproject.integration.domain.timetable

class RecurrentEvent(
    title: String?,
    category: EventCategory,
    location: List<String>,
    val beginTime: String,
    val duration: String,
    val weekdays: List<Weekday>
) : Event(title, description, category, location)
