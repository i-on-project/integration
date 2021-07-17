package org.ionproject.integration.domain.common

import java.net.URI

data class InstitutionModel(
    val name: String,
    val acronym: String,
    val identifier: String,
    val timezone: String,
    val academicCalendarUri: URI
)
