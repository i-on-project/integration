package org.ionproject.integration.application.dto

data class ProgrammeMetadata(
    val institution: InstitutionMetadata,
    val name: String,
    val acronym: String
)