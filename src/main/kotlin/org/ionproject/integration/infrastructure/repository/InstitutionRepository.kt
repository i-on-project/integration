package org.ionproject.integration.infrastructure.repository

import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.SupportedInstitutions
import org.ionproject.integration.infrastructure.exception.ArgumentException
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
            ?: throw ArgumentException("Institution with identifier $identifier not found")
}
