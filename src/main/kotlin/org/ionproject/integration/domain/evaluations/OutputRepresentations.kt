package org.ionproject.integration.domain.evaluations

import org.ionproject.integration.domain.common.dto.SchoolDto

data class EvaluationsDto(
    val creationDateTime: String = "",
    val retrievalDateTime: String = "",
    val school: SchoolDto,
    val calendarTerm: String,
    val exams: List<ExamDto>
) {
    companion object {
        fun from(evaluations: Evaluations): EvaluationsDto {
            return EvaluationsDto(
                evaluations.creationDateTime,
                evaluations.retrievalDateTime,
                SchoolDto(
                    evaluations.school.name,
                    evaluations.school.acr
                ),
                evaluations.calendarTerm,
                emptyList()
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
)
