package org.ionproject.integration.domain.timetable

data class Course(
    val label: Label,
    var events: List<RecurrentEvent>
)
