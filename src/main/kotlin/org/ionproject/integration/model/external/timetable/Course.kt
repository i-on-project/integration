package org.ionproject.integration.model.external.timetable

data class Course(
    val label: Label,
    var events: List<Event>
)
