package org.ionproject.integration.domain.timetable.model

data class Course(
    val label: Label,
    var events: List<RecurrentEvent>
)
