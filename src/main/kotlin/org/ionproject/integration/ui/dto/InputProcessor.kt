package org.ionproject.integration.ui.dto

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.application.job.JobType
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.repository.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.IProgrammeRepository
import org.springframework.stereotype.Component

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

    private fun validateDto(dto: CreateJobDto, jobType: JobType) {
        runCatching {
            requireNotNull(dto.format) { "format" }
            requireNotNull(dto.institution) { "institution" }

            if (jobType == JobType.TIMETABLE || jobType == JobType.EXAM_SCHEDULE)
                requireNotNull(dto.programme) { "programme" }
        }.onFailure { throw ArgumentException("Parameter '${it.message}' missing") }
    }
}
