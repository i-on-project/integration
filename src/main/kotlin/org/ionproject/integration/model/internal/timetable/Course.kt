package org.ionproject.integration.model.internal.timetable

data class Course(
    val label: Label,
    var events: List<Event>
)
