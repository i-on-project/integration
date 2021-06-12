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
        val safeDto = dto.toSafeDto()

        val format = OutputFormat.of(safeDto.format)
        val institution = institutionRepo.getInstitutionByIdentifier(safeDto.institution)
        val programme = programmeRepo.getProgrammeByAcronymAndInstitution(safeDto.programme, institution)

        return JobEngine.TimetableJobRequest(format, institution, programme)
    }

    fun getCalendarJobRequest(dto: CreateCalendarJobDto): JobEngine.CalendarJobRequest {
        val safeDto = dto.toSafeDto()

        val format = OutputFormat.of(safeDto.format)
        val institution = institutionRepo.getInstitutionByIdentifier(safeDto.institution)

        return JobEngine.CalendarJobRequest(format, institution)
    }
}

sealed interface JobDto {
    val institution: String?
    val format: String?
    fun toSafeDto(): SafeJobDto
}

sealed interface SafeJobDto {
    val institution: String
    val format: String
}

data class CreateTimetableJobDto(
    override val institution: String? = null,
    val programme: String? = null,
    override val format: String? = null
) : JobDto {
    override fun toSafeDto(): SafeTimetableJobDto {
        validateDto(this)
        return SafeTimetableJobDto(institution!!, programme!!, format!!)
    }
}

data class CreateCalendarJobDto(
    override val institution: String? = null,
    override val format: String? = null
) : JobDto {
    override fun toSafeDto(): SafeCalendarJobDto {
        validateDto(this)
        return SafeCalendarJobDto(institution!!, format!!)
    }
}

data class SafeTimetableJobDto(
    override val institution: String,
    val programme: String,
    override val format: String,
) : SafeJobDto

data class SafeCalendarJobDto(
    override val institution: String,
    override val format: String,
) : SafeJobDto

private fun validateDto(dto: JobDto) {
    runCatching {
        requireNotNull(dto.format) { "format" }
        requireNotNull(dto.institution) { "institution" }
        if (dto is CreateTimetableJobDto)
            requireNotNull(dto.programme) { "programme" }
    }.onFailure { throw ArgumentException("Parameter '${it.message}' missing") }
}
