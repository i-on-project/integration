package org.ionproject.integration.infrastructure.repository

import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.domain.common.SupportedProgrammes
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.springframework.stereotype.Service

interface IProgrammeRepository {
    fun getProgrammeByAcronymAndInstitution(acronym: String, institution: InstitutionModel): ProgrammeModel
}

@Service
class ProgrammeRepositoryImpl : IProgrammeRepository {
    override fun getProgrammeByAcronymAndInstitution(acronym: String, institution: InstitutionModel): ProgrammeModel =
        SupportedProgrammes.values()
            .map(SupportedProgrammes::programme)
            .firstOrNull { it.institutionModel == institution && it.acronym.equals(acronym, ignoreCase = true) }
            ?: throw ArgumentException("Programme $acronym in institution ${institution.identifier} not found")
}
