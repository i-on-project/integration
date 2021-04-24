package org.ionproject.integration.model.external.timetable

class NonRecurrentEvent(
    title: String?,
    description: String = "",
    category: EventCategory,
    location: List<String>,
    val startDate: String,
    val endDate: String
) : Event(title, description, category, location)
