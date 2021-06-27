package org.ionproject.integration.domain.common

import java.net.URI

data class ProgrammeModel(
    val institutionModel: InstitutionModel,
    val name: String,
    val acronym: String,
    val timetableUri: URI
)
