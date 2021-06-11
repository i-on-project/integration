package org.ionproject.integration.ui.dto

import org.ionproject.integration.JobEngine
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.infrastructure.error.ArgumentException
import org.ionproject.integration.infrastructure.repository.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.IProgrammeRepository
import org.springframework.stereotype.Component

@Component
class InputDto(
    val institutionRepo: IInstitutionRepository,
    val programmeRepo: IProgrammeRepository
) {
    fun getTimetableJobRequest(timetableJobDto: CreateTimetableJobDto): JobEngine.TimetableJobRequest {
        runCatching {
            requireNotNull(timetableJobDto.format) { "format" }
            requireNotNull(timetableJobDto.institution) { "institution" }
            requireNotNull(timetableJobDto.programme) { "programme" }
        }.onFailure { throw ArgumentException("Parameter '${it.message}' missing") }

        val format = OutputFormat.of(timetableJobDto.format!!)
        val institution = institutionRepo.getInstitutionByIdentifier(timetableJobDto.institution!!)
        val programme = programmeRepo.getProgrammeByAcronymAndInstitution(timetableJobDto.programme!!, institution)

        return JobEngine.TimetableJobRequest(format, institution, programme)
    }
}

data class CreateTimetableJobDto(
    val institution: String? = null,
    val programme: String? = null,
    val format: String? = null
)
