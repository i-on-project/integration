package org.ionproject.integration.domain.common

data class ProgrammeModel(
    val institutionModel: InstitutionModel,
    val name: String,
    val acronym: String,
    val resources: ProgrammeResources
)
