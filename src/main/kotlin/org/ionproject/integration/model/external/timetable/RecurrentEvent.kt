package org.ionproject.integration.model.external.timetable

class RecurrentEvent(
    title: String?,
    description: String = "",
    category: EventCategory,
    location: List<String>,
    val beginTime: String,
    val duration: String,
    val weekday: List<String>
) : Event(title, description, category, location)
