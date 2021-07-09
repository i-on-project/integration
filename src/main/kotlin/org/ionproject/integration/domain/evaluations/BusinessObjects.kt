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
import org.ionproject.integration.infrastructure.text.RegexUtils
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
        private const val CALENDAR_TERM_REGEX = "(verão|inverno) \\d{4}\\/\\d{4}"
        private const val SUMMER_TERM = "verão"
        private const val WINTER_TERM = "inverno"

        fun from(rawEvaluationsData: RawEvaluationsData, jobProgramme: ProgrammeModel): Evaluations {
            val calendarTerm = buildCalendarTerm(rawEvaluationsData)

            return Evaluations(
                rawEvaluationsData.creationDate,
                DateUtils.formatToISO8601(ZonedDateTime.now()),
                School(
                    jobProgramme.institutionModel.name,
                    jobProgramme.institutionModel.acronym
                ),
                Programme(
                    jobProgramme.name,
                    jobProgramme.acronym
                ),
                calendarTerm,
                buildExamList(rawEvaluationsData, jobProgramme, getCalendarYear(calendarTerm))
            )
        }

        private fun getCalendarYear(calendarTerm: String) =
            when (val termNumber = calendarTerm.last()) {
                '1' -> calendarTerm.take(4)
                '2' -> calendarTerm.substring(5, 9)
                else -> throw IllegalArgumentException("Invalid term description: $termNumber")
            }

        private fun buildCalendarTerm(rawEvaluationsData: RawEvaluationsData): String {
            val calendarTerm =
                RegexUtils.findMatches(CALENDAR_TERM_REGEX, rawEvaluationsData.textData.toString()).first()
                    .replace("/", "-")

            val termNumber = when (val termType = calendarTerm.substringBefore(" ").lowercase()) {
                WINTER_TERM -> 1
                SUMMER_TERM -> 2
                else -> throw IllegalArgumentException("Invalid term description: $termType")
            }

            return calendarTerm.substringAfter(" ") + "-" + termNumber
        }

        private fun buildExamList(
            rawEvaluationsData: RawEvaluationsData,
            jobProgramme: ProgrammeModel,
            year: String
        ): List<Exam> =
            rawEvaluationsData.table.toTableList().map { getExamsFromTable(it, jobProgramme.acronym, year) }.orThrow()

        private fun String.toTableList(): Try<List<Table>> =
            JsonUtils.fromJson(this, Types.newParameterizedType(List::class.java, Table::class.java))

        private fun getExamsFromTable(
            tableList: List<Table>,
            programmeAcronym: String,
            year: String
        ): List<Exam> {
            val examList = mutableListOf<Exam>()
            for (table in tableList) {
                for (line in table.data) {
                    val cleanedLine = line.dropWhile { it.text.isBlank() }
                    val courseAcronym = trimCourse(cleanedLine[TableColumn.COURSE.ordinal].text)
                    if (cleanedLine[TableColumn.SUMMER_EXAM_PROGRAMME.ordinal].text.contains(programmeAcronym)) {
                        val intervalDateTimeNormal =
                            DateUtils.getEvaluationDateTimeFrom(
                                year,
                                cleanedLine[TableColumn.NORMAL_EXAM_DATE.ordinal].text,
                                cleanedLine[TableColumn.NORMAL_EXAM_TIME.ordinal].text,
                                cleanedLine[TableColumn.NORMAL_EXAM_DURATION.ordinal].text
                            )
                        examList.add(
                            Exam(
                                courseAcronym,
                                intervalDateTimeNormal.from,
                                intervalDateTimeNormal.to,
                                ExamCategory.EXAM_NORMAL,
                                ""
                            )
                        )
                        val intervalDateTimeAltern =
                            DateUtils.getEvaluationDateTimeFrom(
                                year,
                                cleanedLine[TableColumn.ALTERN_EXAM_DATE.ordinal].text,
                                cleanedLine[TableColumn.ALTERN_EXAM_TIME.ordinal].text,
                                cleanedLine[TableColumn.ALTERN_EXAM_DURATION.ordinal].text
                            )
                        examList.add(
                            Exam(
                                courseAcronym,
                                intervalDateTimeAltern.from,
                                intervalDateTimeAltern.to,
                                ExamCategory.EXAM_ALTERN,
                                ""
                            )
                        )
                        val intervalDateTimeSpecial =
                            DateUtils.getEvaluationDateTimeFrom(
                                year,
                                cleanedLine[TableColumn.SPECIAL_EXAM_DATE.ordinal].text,
                                cleanedLine[TableColumn.SPECIAL_EXAM_TIME.ordinal].text,
                                cleanedLine[TableColumn.SPECIAL_EXAM_DURATION.ordinal].text
                            )
                        examList.add(
                            Exam(
                                courseAcronym,
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
                                year,
                                cleanedLine[TableColumnWinterCourse.SPECIAL_EXAM_DATE.ordinal].text,
                                cleanedLine[TableColumnWinterCourse.SPECIAL_EXAM_TIME.ordinal].text,
                                cleanedLine[TableColumnWinterCourse.SPECIAL_EXAM_DURATION.ordinal].text
                            )
                        examList.add(
                            Exam(
                                courseAcronym,
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

        /**
         * Removes any substring from the course acronum that starts with hyphen.
         * Examples: SO-leect-leirt, SS-leetc
         */
        private fun trimCourse(course: String) =
            course.substringBefore("-")
    }
}

data class Exam(
    val course: String,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
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
