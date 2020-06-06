package org.ionproject.integration.model.internal.timetable

data class School(
    var name: String = "",
    @Transient var acr: String = ""
)
