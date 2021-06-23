package org.ionproject.integration.ui.dto

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.ionproject.integration.infrastructure.repository.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.IProgrammeRepository

data class SafeTimetableJobDto(
    override val institution: String,
    val programme: String,
    override val format: String,
) : SafeJobDto {
    override fun toJobRequest(
        institutionRepo: IInstitutionRepository,
        programmeRepo: IProgrammeRepository
    ): JobEngine.AbstractJobRequest {
        val format = OutputFormat.of(format)
        val institution = institutionRepo.getInstitutionByIdentifier(institution)
        val programme = programmeRepo.getProgrammeByAcronymAndInstitution(programme, institution)

        return JobEngine.TimetableJobRequest(format, institution, programme)
    }
}
