package org.ionproject.integration.domain.evaluations

import org.ionproject.integration.domain.common.School
import org.ionproject.integration.infrastructure.DateUtils
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class Evaluations(
    val creationDateTime: String = "",
    val retrievalDateTime: String = "",
    val school: School = School(),
    val calendarTerm: String = "",
    val exams: List<Exam>
) {
    companion object {
        fun from(rawEvaluationsData: RawEvaluationsData): Evaluations =
            Evaluations(
                creationDateTime = rawEvaluationsData.creationDate,
                retrievalDateTime = DateUtils.formatToISO8601(ZonedDateTime.now()),
                School(
                    "Instituto Superior de Engenharia de Lisboa",
                    "ISEL"
                ),
                calendarTerm = buildCalendarTerm(rawEvaluationsData),
                emptyList()
            )

        // TODO
        private fun buildCalendarTerm(rawEvaluationsData: RawEvaluationsData): String = ""
    }
}

data class Exam(
    val course: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val category: ExamCategory,
    val location: String
)

enum class ExamCategory {
    TEST,
    EXAM_NORMAL,
    EXAM_ALTERN,
    EXAM_SPECIAL
}
