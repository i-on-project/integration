package org.ionproject.integration.domain.evaluations

import org.ionproject.integration.domain.common.dto.ProgrammeDto
import org.ionproject.integration.domain.common.dto.SchoolDto
import org.ionproject.integration.infrastructure.DateUtils

data class EvaluationsDto(
    val creationDateTime: String = "",
    val retrievalDateTime: String = "",
    val school: SchoolDto,
    val programme: ProgrammeDto,
    val calendarTerm: String,
    val exams: List<ExamDto>
) {
    companion object {
        fun from(evaluations: Evaluations): EvaluationsDto {
            return EvaluationsDto(
                DateUtils.formatToISO8601(evaluations.creationDateTime),
                DateUtils.formatToISO8601(evaluations.retrievalDateTime),
                SchoolDto(
                    evaluations.school.name,
                    evaluations.school.acr
                ),
                ProgrammeDto(
                    evaluations.programme.name,
                    evaluations.programme.acr
                ),
                evaluations.calendarTerm.toString(),
                ExamDto.from(evaluations.exams)
            )
        }
    }
}

data class ExamDto(
    val course: String,
    val startDate: String,
    val endDate: String,
    val category: String,
    val location: String
) {
    companion object {
        fun from(exams: List<Exam>): List<ExamDto> = exams.map {
            ExamDto(
                it.course,
                DateUtils.formatToISO8601(it.startDate),
                DateUtils.formatToISO8601(it.endDate),
                it.category.name,
                it.location
            )
        }
    }
}
