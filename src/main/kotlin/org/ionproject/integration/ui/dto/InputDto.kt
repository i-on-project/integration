package org.ionproject.integration.ui.dto

import org.ionproject.integration.JobEngine
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.infrastructure.IntegrationException
import org.ionproject.integration.infrastructure.repository.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.IProgrammeRepository
import org.springframework.stereotype.Component

@Component
class InputDto(
    val institutionRepo: IInstitutionRepository,
    val programmeRepo: IProgrammeRepository
) {
    fun getTimetableJobRequest(timetableJobDto: CreateTimetableJobDto): JobEngine.TimetableJobRequest {
        if (timetableJobDto.format == null)
            throw IntegrationException("Format cannot be empty")

        if (timetableJobDto.institution == null)
            throw IntegrationException("Institution identifier cannot be empty")

        if (timetableJobDto.programme == null)
            throw IntegrationException("Programme acronym cannot be empty")

        val format = OutputFormat.of(timetableJobDto.format)
        val institution = institutionRepo.getInstitutionByIdentifier(timetableJobDto.institution)
        val programme = programmeRepo.getProgrammeByAcronymAndInstitution(timetableJobDto.programme, institution)

        return JobEngine.TimetableJobRequest(format, institution, programme)
    }
}

data class CreateTimetableJobDto(
    val institution: String? = null,
    val programme: String? = null,
    val format: String? = null
)
