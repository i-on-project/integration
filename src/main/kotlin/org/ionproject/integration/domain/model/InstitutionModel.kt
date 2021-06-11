package org.ionproject.integration.domain.model

import java.net.URI

data class InstitutionModel(
    val name: String,
    val acronym: String,
    val identifier: String,
    val academicCalendarUri: URI
)

// TODO: This should eventually be replaced by database data
enum class SupportedInstitutions(val institution: InstitutionModel) {
    ISEL(
        InstitutionModel(
            "Instituto Superior de Engenharia de Lisboa",
            "ISEL",
            "pt.ipl.isel",
            URI("https://www.isel.pt/media/uploads/OS09P2020_signed.pdf")
        )
    )
}
