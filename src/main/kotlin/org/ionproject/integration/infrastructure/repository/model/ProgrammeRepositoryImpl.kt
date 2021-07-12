package org.ionproject.integration.infrastructure.repository.model

import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class ProgrammeRepositoryImpl(private val institutionRepo: InstitutionRepositoryImpl) : IProgrammeRepository {
    override fun getProgrammeByAcronymAndInstitution(acronym: String, institution: InstitutionModel): ProgrammeModel {
        val institutions = institutionRepo.readInstitutionsFromFile()
        val institutionDto = institutionRepo.findInstitution(institution.identifier, institutions)

        return institutionDto.programmes
            .map { it.toModel(institution) }
            .firstOrNull { it.acronym.equals(acronym, ignoreCase = true) }
            ?: throw ArgumentException("Programme $acronym in institution ${institution.identifier} not found")
    }
}
