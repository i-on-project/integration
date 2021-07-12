package org.ionproject.integration.ui.input

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.application.job.JobType
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.repository.model.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.model.IProgrammeRepository
import org.springframework.stereotype.Component

internal const val INVALID_JOB_TYPE_ERROR = "Invalid Job Type: %s"
internal const val MISSING_PARAMETER_ERROR = "Parameter '%s' missing"
internal const val FORMAT = "format"
internal const val INSTITUTION = "institution"
internal const val PROGRAMME = "programme"

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
        val type = JobType.of(dto.type) ?: throw ArgumentException(INVALID_JOB_TYPE_ERROR.format(dto.type?.trim()))
        validateDto(dto, type)

        return dto.toSafeDto(type)
    }

    private fun validateDto(dto: CreateJobDto, jobType: JobType) {
        runCatching {
            require(dto.format?.isNotBlank() ?: false) { FORMAT }
            require(dto.institution?.isNotBlank() ?: false) { INSTITUTION }

            if (jobType == JobType.TIMETABLE || jobType == JobType.EXAM_SCHEDULE)
                require(dto.programme?.isNotBlank() ?: false) { PROGRAMME }
        }.onFailure { throw ArgumentException(MISSING_PARAMETER_ERROR.format(it.message)) }
    }
}
