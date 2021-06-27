package org.ionproject.integration.infrastructure.repository

import org.ionproject.integration.domain.common.InstitutionModel

interface IInstitutionRepository {
    fun getInstitutionByIdentifier(identifier: String): InstitutionModel
}
