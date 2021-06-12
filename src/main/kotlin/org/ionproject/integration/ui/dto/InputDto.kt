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
    fun getTimetableJobRequest(dto: CreateTimetableJobDto): JobEngine.TimetableJobRequest {
        runCatching {
            requireNotNull(dto.format) { "format" }
            requireNotNull(dto.institution) { "institution" }
            requireNotNull(dto.programme) { "programme" }
        }.onFailure { throw ArgumentException("Parameter '${it.message}' missing") }

        val format = OutputFormat.of(dto.format!!)
        val institution = institutionRepo.getInstitutionByIdentifier(dto.institution!!)
        val programme = programmeRepo.getProgrammeByAcronymAndInstitution(dto.programme!!, institution)

        return JobEngine.TimetableJobRequest(format, institution, programme)
    }

    fun getCalendarJobRequest(dto: CreateCalendarJobDto): JobEngine.CalendarJobRequest {
        runCatching {
            requireNotNull(dto.format) { "format" }
            requireNotNull(dto.institution) { "institution" }
        }.onFailure { throw ArgumentException("Parameter '${it.message}' missing") }

        val format = OutputFormat.of(dto.format!!)
        val institution = institutionRepo.getInstitutionByIdentifier(dto.institution!!)

        return JobEngine.CalendarJobRequest(format, institution)
    }
}

data class CreateTimetableJobDto(
    val institution: String? = null,
    val programme: String? = null,
    val format: String? = null
)

data class CreateCalendarJobDto(
    val institution: String? = null,
    val format: String? = null
)
