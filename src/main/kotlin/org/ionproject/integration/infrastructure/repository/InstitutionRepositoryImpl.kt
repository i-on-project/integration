package org.ionproject.integration.infrastructure.repository

import org.ionproject.integration.domain.model.InstitutionModel
import org.ionproject.integration.domain.model.SupportedInstitutions
import org.ionproject.integration.infrastructure.IntegrationException
import org.springframework.stereotype.Service

interface IInstitutionRepository {
    fun getInstitutionByIdentifier(identifier: String): InstitutionModel
}

@Service
class InstitutionRepositoryImpl : IInstitutionRepository {
    override fun getInstitutionByIdentifier(identifier: String): InstitutionModel =
        SupportedInstitutions.values()
            .map(SupportedInstitutions::institution)
            .firstOrNull { it.identifier == identifier }
            ?: throw IntegrationException("Institution with identifier $identifier not found")
}
