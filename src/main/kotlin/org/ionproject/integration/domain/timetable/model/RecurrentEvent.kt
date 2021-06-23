package org.ionproject.integration.domain.timetable.model

import org.ionproject.integration.domain.common.Weekday

class RecurrentEvent(
    title: String?,
    category: EventCategory,
    location: List<String>,
    val beginTime: String,
    val duration: String,
    val weekdays: List<Weekday>
) : Event(title, category, location)
