package org.ionproject.integration.model.external.timetable

data class Faculty(
    val label: Label? = null,
    var teachers: List<Teacher> = listOf()
)
