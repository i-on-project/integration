package org.ionproject.integration.domain.model

import java.net.URI

data class ProgrammeModel(
    val institutionModel: InstitutionModel,
    val name: String,
    val acronym: String,
    val timetableUrl: URI
)

// TODO: This should eventually be replaced by database data
enum class SupportedProgrammes(val programme: ProgrammeModel) {
    LEETC(
        ProgrammeModel(
            SupportedInstitutions.ISEL.institution,
            "Licenciatura em Engenharia Electrónica e Telecomunicações e de Computadores",
            "LEETC",
            URI("https://www.isel.pt/media/uploads/ADEETC_LEETC_210322.pdf")
        )
    ),
    LEIC(
        ProgrammeModel(
            SupportedInstitutions.ISEL.institution,
            "Licenciatura em Engenharia Informática e de Computadores",
            "LEIC",
            URI("https://www.isel.pt/media/uploads/ADEETC_LEIC_210322.pdf")
        )
    ),
    LEIM(
        ProgrammeModel(
            SupportedInstitutions.ISEL.institution,
            "Licenciatura em Engenharia Informática e Multimédia",
            "LEIM",
            URI("https://www.isel.pt/media/uploads/ADEETC_LEIM_210228.pdf")
        )
    ),
    LEIRT(
        ProgrammeModel(
            SupportedInstitutions.ISEL.institution,
            "Licenciatura em Engenharia Informática, Redes e Telecomunicações",
            "LEIRT",
            URI("https://www.isel.pt/media/uploads/ADEETC_LEIRT_210318.pdf")
        )
    ),
    MEIC(
        ProgrammeModel(
            SupportedInstitutions.ISEL.institution,
            "Mestrado em Engenharia Informática e de Computadores",
            "MEIC",
            URI("https://www.isel.pt/media/uploads/ADEETC_MEIC_210301.pdf")
        )
    ),
    MEIM(
        ProgrammeModel(
            SupportedInstitutions.ISEL.institution,
            "Mestrado em Engenharia Informática e Multimédia",
            "MEIM",
            URI("https://www.isel.pt/media/uploads/ADEETC_MEIM_210228.pdf")
        )
    )
}
