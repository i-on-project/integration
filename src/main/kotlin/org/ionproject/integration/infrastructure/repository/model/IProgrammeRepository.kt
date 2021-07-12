package org.ionproject.integration.infrastructure.repository.model

import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.ProgrammeModel

interface IProgrammeRepository {
    fun getProgrammeByAcronymAndInstitution(acronym: String, institution: InstitutionModel): ProgrammeModel
}
