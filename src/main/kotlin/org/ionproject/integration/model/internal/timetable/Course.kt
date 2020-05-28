package org.ionproject.integration.model.internal.timetable

data class Course(
    val acronym: String,
    val type: String,
    val room: String,
    val begin_time: String,
    val end_time: String,
    val duration: String,
    val weekday: String
)
