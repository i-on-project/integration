package org.ionproject.integration.ui.dto

import org.ionproject.integration.JobEngine
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.infrastructure.error.ArgumentException
import org.ionproject.integration.infrastructure.repository.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.IProgrammeRepository
import org.springframework.stereotype.Component
import java.lang.UnsupportedOperationException

@Component
class InputProcessor(
    val institutionRepo: IInstitutionRepository,
    val programmeRepo: IProgrammeRepository
) {
    fun getJobRequest(dto: CreateJobDto): JobEngine.AbstractJobRequest {
        val safeDto = getValidatedDto(dto)
        return safeDto.toJobRequest(institutionRepo, programmeRepo)
    }

    private fun getValidatedDto(dto: CreateJobDto): SafeJobDto {
        val type = JobType.of(dto.type) ?: throw ArgumentException("Invalid Job Type: ${dto.type}")
        validateDto(dto, type)

        return dto.toSafeDto(type)
    }
}

enum class JobType(val identifier: String) {
    TIMETABLE("timetable"),
    EXAM_SCHEDULE("evaluation"),
    ACADEMIC_CALENDAR("calendar");

    companion object Factory {
        fun of(name: String?): JobType? = values().find { it.identifier.equals(name?.trim(), ignoreCase = true) }
    }
}

sealed interface SafeJobDto {
    val institution: String
    val format: String

    fun toJobRequest(
        institutionRepo: IInstitutionRepository,
        programmeRepo: IProgrammeRepository
    ): JobEngine.AbstractJobRequest
}

data class CreateJobDto(
    val institution: String? = null,
    val programme: String? = null,
    val format: String? = null,
    val type: String? = null
) {
    fun toSafeDto(type: JobType): SafeJobDto {
        return when (type) {
            JobType.TIMETABLE -> SafeTimetableJobDto(institution!!, programme!!, format!!)
            JobType.ACADEMIC_CALENDAR -> SafeCalendarJobDto(institution!!, format!!)
            JobType.EXAM_SCHEDULE -> throw UnsupportedOperationException("Exam Schedule not yet supported")
        }
    }
}

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

data class SafeCalendarJobDto(
    override val institution: String,
    override val format: String,
) : SafeJobDto {
    override fun toJobRequest(
        institutionRepo: IInstitutionRepository,
        programmeRepo: IProgrammeRepository
    ): JobEngine.AbstractJobRequest {
        val format = OutputFormat.of(format)
        val institution = institutionRepo.getInstitutionByIdentifier(institution)

        return JobEngine.CalendarJobRequest(format, institution)
    }
}

private fun validateDto(dto: CreateJobDto, jobType: JobType) {
    runCatching {
        requireNotNull(dto.format) { "format" }
        requireNotNull(dto.institution) { "institution" }

        if (jobType == JobType.TIMETABLE || jobType == JobType.EXAM_SCHEDULE)
            requireNotNull(dto.programme) { "programme" }
    }.onFailure { throw ArgumentException("Parameter '${it.message}' missing") }
}
