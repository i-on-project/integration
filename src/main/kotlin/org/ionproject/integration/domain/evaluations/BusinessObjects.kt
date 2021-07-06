package org.ionproject.integration.domain.evaluations

import com.squareup.moshi.Types
import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.infrastructure.DateUtils
import org.ionproject.integration.infrastructure.Try
import org.ionproject.integration.infrastructure.orThrow
import org.ionproject.integration.infrastructure.pdfextractor.tabula.Table
import org.ionproject.integration.infrastructure.text.JsonUtils
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class Evaluations(
    val creationDateTime: String = "",
    val retrievalDateTime: String = "",
    val school: School,
    val programme: Programme,
    val calendarTerm: String = "",
    val exams: List<Exam>
) {
    companion object {
        private const val CALENDAR_TERM_REGEX = "(\\sAno\\sLetivo\\s?:\\s?)(.+?(\\r|\\R))"

        fun from(rawEvaluationsData: RawEvaluationsData, jobProgramme: ProgrammeModel): Evaluations {

            return Evaluations(
                creationDateTime = rawEvaluationsData.creationDate,
                retrievalDateTime = DateUtils.formatToISO8601(ZonedDateTime.now()),
                School(
                    jobProgramme.institutionModel.name,
                    jobProgramme.institutionModel.acronym
                ),
                Programme(
                    jobProgramme.name,
                    jobProgramme.acronym
                ),
                calendarTerm = buildCalendarTerm(rawEvaluationsData),
                buildExamList(rawEvaluationsData, jobProgramme)
            )
        }

        // TODO
        private fun buildCalendarTerm(rawEvaluationsData: RawEvaluationsData): String = "2020-2021-2"

        private fun buildExamList(rawEvaluationsData: RawEvaluationsData, jobProgramme: ProgrammeModel): List<Exam> =
            rawEvaluationsData.table.toTableList().map { getExamsFromTable(it, jobProgramme.acronym) }.orThrow()

        private fun String.toTableList(): Try<List<Table>> =
            JsonUtils.fromJson(this, Types.newParameterizedType(List::class.java, Table::class.java))

        private fun getExamsFromTable(
            tableList: List<Table>,
            programmeAcronym: String
        ): List<Exam> {
            val examList = mutableListOf<Exam>()
            for (table in tableList) {
                for (line in table.data) {
                    val cleanedLine = line.dropWhile { it.text.isBlank() }
                    if (cleanedLine[TableColumn.SUMMER_EXAM_PROGRAMME.ordinal].text.contains(programmeAcronym)) {
                        val intervalDateTimeNormal =
                            DateUtils.getEvaluationDateTimeFrom(
                                "2021",
                                cleanedLine[TableColumn.NORMAL_EXAM_DATE.ordinal].text,
                                cleanedLine[TableColumn.NORMAL_EXAM_TIME.ordinal].text,
                                cleanedLine[TableColumn.NORMAL_EXAM_DURATION.ordinal].text
                            )
                        examList.add(
                            Exam(
                                cleanedLine[TableColumn.COURSE.ordinal].text,
                                intervalDateTimeNormal.from,
                                intervalDateTimeNormal.to,
                                ExamCategory.EXAM_NORMAL,
                                ""
                            )
                        )
                        val intervalDateTimeAltern =
                            DateUtils.getEvaluationDateTimeFrom(
                                "2021",
                                cleanedLine[TableColumn.ALTERN_EXAM_DATE.ordinal].text,
                                cleanedLine[TableColumn.ALTERN_EXAM_TIME.ordinal].text,
                                cleanedLine[TableColumn.ALTERN_EXAM_DURATION.ordinal].text
                            )
                        examList.add(
                            Exam(
                                cleanedLine[TableColumn.COURSE.ordinal].text,
                                intervalDateTimeAltern.from,
                                intervalDateTimeAltern.to,
                                ExamCategory.EXAM_ALTERN,
                                ""
                            )
                        )
                        val intervalDateTimeSpecial =
                            DateUtils.getEvaluationDateTimeFrom(
                                "2021",
                                cleanedLine[TableColumn.SPECIAL_EXAM_DATE.ordinal].text,
                                cleanedLine[TableColumn.SPECIAL_EXAM_TIME.ordinal].text,
                                cleanedLine[TableColumn.SPECIAL_EXAM_DURATION.ordinal].text
                            )
                        examList.add(
                            Exam(
                                cleanedLine[TableColumn.COURSE.ordinal].text,
                                intervalDateTimeSpecial.from,
                                intervalDateTimeSpecial.to,
                                ExamCategory.EXAM_SPECIAL,
                                ""
                            )
                        )
                    }
                    if (cleanedLine[TableColumn.WINTER_EXAM_PROGRAMME.ordinal].text.contains(programmeAcronym) &&
                        !cleanedLine[TableColumn.SUMMER_EXAM_PROGRAMME.ordinal].text.contains(programmeAcronym)
                    ) {
                        val intervalDateTimeSpecial =
                            DateUtils.getEvaluationDateTimeFrom(
                                "2021",
                                cleanedLine[TableColumnWinterCourse.SPECIAL_EXAM_DATE.ordinal].text,
                                cleanedLine[TableColumnWinterCourse.SPECIAL_EXAM_TIME.ordinal].text,
                                cleanedLine[TableColumnWinterCourse.SPECIAL_EXAM_DURATION.ordinal].text
                            )
                        examList.add(
                            Exam(
                                cleanedLine[TableColumnWinterCourse.COURSE.ordinal].text,
                                intervalDateTimeSpecial.from,
                                intervalDateTimeSpecial.to,
                                ExamCategory.EXAM_SPECIAL,
                                ""
                            )
                        )
                    }
                }
            }
            return examList.toList()
        }
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

private enum class TableColumn {
    COURSE,
    WINTER_EXAM_PROGRAMME,
    SUMMER_EXAM_PROGRAMME,
    NORMAL_EXAM_DATE,
    NORMAL_EXAM_TIME,
    NORMAL_EXAM_DURATION,
    ALTERN_EXAM_DATE,
    ALTERN_EXAM_TIME,
    ALTERN_EXAM_DURATION,
    SPECIAL_EXAM_DATE,
    SPECIAL_EXAM_TIME,
    SPECIAL_EXAM_DURATION
}

private enum class TableColumnWinterCourse {
    COURSE,
    WINTER_EXAM_PROGRAMME,
    SUMMER_EXAM_PROGRAMME,
    WINTER_COURSE,
    SPECIAL_EXAM_DATE,
    SPECIAL_EXAM_TIME,
    SPECIAL_EXAM_DURATION
}
