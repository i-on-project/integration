package org.ionproject.integration.domain.evaluations

import com.squareup.moshi.Types
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.infrastructure.DateUtils
import org.ionproject.integration.infrastructure.Try
import org.ionproject.integration.infrastructure.pdfextractor.tabula.Table
import org.ionproject.integration.infrastructure.text.JsonUtils
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
        fun from(rawEvaluationsData: RawEvaluationsData, jobInstitution: InstitutionModel): Evaluations {

            return Evaluations(
                creationDateTime = rawEvaluationsData.creationDate,
                retrievalDateTime = DateUtils.formatToISO8601(ZonedDateTime.now()),
                School(
                    jobInstitution.name,
                    jobInstitution.acronym
                ),
                calendarTerm = buildCalendarTerm(rawEvaluationsData),
                emptyList()
            )
        }

        private fun rawDataToBusiness(rawEvaluationsData: RawEvaluationsData) {
            fun String.toTableList(): Try<List<Table>> =
                JsonUtils.fromJson(this, Types.newParameterizedType(List::class.java, Table::class.java))

            rawEvaluationsData.table.toTableList().map { mapTablesToBusiness(rawEvaluationsData, it) }
        }

        private fun mapTablesToBusiness(
            rawEvaluationsData: RawEvaluationsData,
            tableList: List<Table>
        ) {
        }

        // TODO
        private fun buildCalendarTerm(rawEvaluationsData: RawEvaluationsData): String = "2020-2021-2"
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
