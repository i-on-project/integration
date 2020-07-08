package org.ionproject.integration.model.external.timetable

data class Programme(
    var name: String = "",
    @Transient var acr: String = ""
)
