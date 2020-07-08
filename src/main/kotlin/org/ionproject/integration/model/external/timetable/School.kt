package org.ionproject.integration.model.external.timetable

data class School(
    var name: String = "",
    @Transient var acr: String = ""
)
