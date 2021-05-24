package org.ionproject.integration.model.external.timetable

class RecurrentEvent(
    title: String?,
    description: String = "",
    category: EventCategory,
    location: List<String>,
    val beginTime: String,
    val duration: String,
    val weekdays: List<Weekday>
) : Event(title, description, category, location)
