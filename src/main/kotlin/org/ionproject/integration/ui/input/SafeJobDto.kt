package org.ionproject.integration.ui.input

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.infrastructure.repository.model.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.model.IProgrammeRepository

interface SafeJobDto {
    val institution: String
    val format: String

    fun toJobRequest(
        institutionRepo: IInstitutionRepository,
        programmeRepo: IProgrammeRepository
    ): JobEngine.AbstractJobRequest
}
