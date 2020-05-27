package org.ionproject.integration.model.internal.timetable

data class Course(
    var acronym: String = "",
    var type: String = "",
    var room: String = "",
    var begin_time: String = "",
    var end_time: String = "",
    var duration: String = "",
    var weekday: String = ""
)
