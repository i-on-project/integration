package org.ionproject.integration.domain.evaluations

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ionproject.integration.domain.common.CalendarTerm
import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.common.Term
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Year
import java.time.ZoneId
import java.time.ZonedDateTime

internal class EvaluationsDtoFormatCheckerTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun `when Serialized Evaluations is equal to expected Dto then Success`() {

        val evaluations =
            Evaluations(
                ZonedDateTime.of(2021, 7, 8, 0, 12, 56, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2021, 7, 8, 0, 12, 56, 0, ZoneId.of("UTC")),
                School(
                    "Instituto Superior de Engenharia de Lisboa",
                    "ISEL"
                ),
                Programme(
                    "Licenciatura em Engenharia Informática e de Computadores",
                    "LEIC"
                ),
                CalendarTerm(
                    Year.parse("2020"),
                    Year.parse("2021"),
                    Term.SPRING
                ),
                listOf(
                    Exam(
                        "AApl",
                        ZonedDateTime.of(2021, 7, 7, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 7, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AApl",
                        ZonedDateTime.of(2021, 7, 24, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 24, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AApl",
                        ZonedDateTime.of(2021, 9, 9, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 9, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "AC",
                        ZonedDateTime.of(2021, 7, 1, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 1, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AC",
                        ZonedDateTime.of(2021, 7, 19, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 19, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AC",
                        ZonedDateTime.of(2021, 9, 7, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 7, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "AED",
                        ZonedDateTime.of(2021, 6, 28, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 6, 28, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AED",
                        ZonedDateTime.of(2021, 7, 20, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 20, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AED",
                        ZonedDateTime.of(2021, 9, 16, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 16, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "ALGA",
                        ZonedDateTime.of(2021, 7, 8, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 8, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "ALGA",
                        ZonedDateTime.of(2021, 7, 23, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 23, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "ALGA",
                        ZonedDateTime.of(2021, 9, 8, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 8, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "AVE",
                        ZonedDateTime.of(2021, 7, 5, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 5, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AVE",
                        ZonedDateTime.of(2021, 7, 22, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 22, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AVE",
                        ZonedDateTime.of(2021, 9, 15, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 15, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CDI",
                        ZonedDateTime.of(2021, 7, 16, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 16, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "CDI",
                        ZonedDateTime.of(2021, 7, 30, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 30, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "CDI",
                        ZonedDateTime.of(2021, 9, 17, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 17, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CN",
                        ZonedDateTime.of(2021, 7, 15, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 15, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "CN",
                        ZonedDateTime.of(2021, 7, 29, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 29, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "CN",
                        ZonedDateTime.of(2021, 9, 10, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 10, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "COM",
                        ZonedDateTime.of(2021, 7, 12, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 12, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "COM",
                        ZonedDateTime.of(2021, 7, 26, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 26, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "COM",
                        ZonedDateTime.of(2021, 9, 13, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 13, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CQF",
                        ZonedDateTime.of(2021, 9, 8, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 8, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CSM",
                        ZonedDateTime.of(2021, 7, 12, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 12, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "CSM",
                        ZonedDateTime.of(2021, 7, 31, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 31, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "CSM",
                        ZonedDateTime.of(2021, 9, 8, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 8, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "DAW",
                        ZonedDateTime.of(2021, 6, 28, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 6, 28, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "DAW",
                        ZonedDateTime.of(2021, 7, 21, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 21, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "DAW",
                        ZonedDateTime.of(2021, 9, 15, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 15, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "EGP",
                        ZonedDateTime.of(2021, 7, 1, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 1, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "EGP",
                        ZonedDateTime.of(2021, 7, 20, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 20, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "EGP",
                        ZonedDateTime.of(2021, 9, 9, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 9, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "Eltr",
                        ZonedDateTime.of(2021, 7, 13, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 13, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "Eltr",
                        ZonedDateTime.of(2021, 7, 27, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 27, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "Eltr",
                        ZonedDateTime.of(2021, 9, 15, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 15, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "Emp",
                        ZonedDateTime.of(2021, 9, 16, 17, 30, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 16, 20, 30, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "GAP",
                        ZonedDateTime.of(2021, 6, 30, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 6, 30, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "GAP",
                        ZonedDateTime.of(2021, 7, 19, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 19, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "GAP",
                        ZonedDateTime.of(2021, 9, 3, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 3, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "GQS",
                        ZonedDateTime.of(2021, 9, 7, 17, 30, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 7, 20, 30, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "IASA",
                        ZonedDateTime.of(2021, 7, 2, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 2, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "IASA",
                        ZonedDateTime.of(2021, 7, 20, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 20, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "IASA",
                        ZonedDateTime.of(2021, 9, 2, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 2, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "IEB",
                        ZonedDateTime.of(2021, 9, 6, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 6, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "LC",
                        ZonedDateTime.of(2021, 9, 14, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 14, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "LSD",
                        ZonedDateTime.of(2021, 7, 6, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 6, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "LSD",
                        ZonedDateTime.of(2021, 7, 21, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 21, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "LSD",
                        ZonedDateTime.of(2021, 9, 6, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 6, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PC",
                        ZonedDateTime.of(2021, 7, 13, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 13, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PC",
                        ZonedDateTime.of(2021, 7, 27, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 27, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PC",
                        ZonedDateTime.of(2021, 9, 17, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 17, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PDM",
                        ZonedDateTime.of(2021, 9, 14, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 14, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PE",
                        ZonedDateTime.of(2021, 7, 5, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 5, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PE",
                        ZonedDateTime.of(2021, 7, 22, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 22, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PE",
                        ZonedDateTime.of(2021, 9, 9, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 9, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PG",
                        ZonedDateTime.of(2021, 6, 29, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 6, 29, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PG",
                        ZonedDateTime.of(2021, 7, 23, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 23, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PG",
                        ZonedDateTime.of(2021, 9, 8, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 8, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PI",
                        ZonedDateTime.of(2021, 7, 2, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 2, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PI",
                        ZonedDateTime.of(2021, 7, 21, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 21, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PI",
                        ZonedDateTime.of(2021, 9, 10, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 10, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "POO",
                        ZonedDateTime.of(2021, 9, 14, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 14, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PSC",
                        ZonedDateTime.of(2021, 7, 6, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 6, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PSC",
                        ZonedDateTime.of(2021, 7, 21, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 21, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PSC",
                        ZonedDateTime.of(2021, 9, 10, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 10, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "RCp",
                        ZonedDateTime.of(2021, 7, 14, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 14, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "RCp",
                        ZonedDateTime.of(2021, 7, 28, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 28, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "RCp",
                        ZonedDateTime.of(2021, 9, 13, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 13, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "RI",
                        ZonedDateTime.of(2021, 9, 7, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 7, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SE1",
                        ZonedDateTime.of(2021, 9, 6, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 6, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SE2",
                        ZonedDateTime.of(2021, 7, 9, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 9, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SE2",
                        ZonedDateTime.of(2021, 7, 26, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 26, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SE2",
                        ZonedDateTime.of(2021, 9, 6, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 6, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SG",
                        ZonedDateTime.of(2021, 7, 1, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 1, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SG",
                        ZonedDateTime.of(2021, 7, 20, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 20, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SG",
                        ZonedDateTime.of(2021, 9, 8, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 8, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SegInf",
                        ZonedDateTime.of(2021, 9, 17, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 17, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SI1",
                        ZonedDateTime.of(2021, 6, 30, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 6, 30, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SI1",
                        ZonedDateTime.of(2021, 7, 19, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 19, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SI1",
                        ZonedDateTime.of(2021, 9, 3, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 3, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SI2",
                        ZonedDateTime.of(2021, 7, 8, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 8, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SI2",
                        ZonedDateTime.of(2021, 7, 23, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 23, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SI2",
                        ZonedDateTime.of(2021, 9, 13, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 13, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SM",
                        ZonedDateTime.of(2021, 9, 15, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 15, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SO",
                        ZonedDateTime.of(2021, 7, 9, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 9, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SO",
                        ZonedDateTime.of(2021, 7, 26, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 26, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SO",
                        ZonedDateTime.of(2021, 9, 7, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 7, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "TAR",
                        ZonedDateTime.of(2021, 7, 16, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 16, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "TAR",
                        ZonedDateTime.of(2021, 7, 30, 18, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 30, 21, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "TAR",
                        ZonedDateTime.of(2021, 9, 7, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 7, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "TJ",
                        ZonedDateTime.of(2021, 7, 12, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 12, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "TJ",
                        ZonedDateTime.of(2021, 7, 28, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 28, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "TJ",
                        ZonedDateTime.of(2021, 9, 17, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 17, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "TMD",
                        ZonedDateTime.of(2021, 7, 10, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 10, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "TMD",
                        ZonedDateTime.of(2021, 7, 29, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 7, 29, 12, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "TMD",
                        ZonedDateTime.of(2021, 9, 13, 13, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2021, 9, 13, 16, 0, 0, 0, ZoneId.of("UTC")),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    )
                )
            )
        val serialized = mapper.writeValueAsString(EvaluationsDto.from(evaluations))

        val expectedJson =
            """{"creationDateTime":"2021-07-08T00:12:56Z","retrievalDateTime":"2021-07-08T00:12:56Z","school":{"name":"Instituto Superior de Engenharia de Lisboa","acr":"ISEL"},"programme":{"name":"Licenciatura em Engenharia Informática e de Computadores","acr":"LEIC"},"calendarTerm":"2020-2021-2","exams":[{"course":"AApl","startDate":"2021-07-07T09:00:00Z","endDate":"2021-07-07T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"AApl","startDate":"2021-07-24T09:00:00Z","endDate":"2021-07-24T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"AApl","startDate":"2021-09-09T13:00:00Z","endDate":"2021-09-09T16:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"AC","startDate":"2021-07-01T13:00:00Z","endDate":"2021-07-01T16:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"AC","startDate":"2021-07-19T18:00:00Z","endDate":"2021-07-19T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"AC","startDate":"2021-09-07T09:00:00Z","endDate":"2021-09-07T12:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"AED","startDate":"2021-06-28T18:00:00Z","endDate":"2021-06-28T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"AED","startDate":"2021-07-20T13:00:00Z","endDate":"2021-07-20T16:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"AED","startDate":"2021-09-16T18:00:00Z","endDate":"2021-09-16T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"ALGA","startDate":"2021-07-08T18:00:00Z","endDate":"2021-07-08T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"ALGA","startDate":"2021-07-23T09:00:00Z","endDate":"2021-07-23T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"ALGA","startDate":"2021-09-08T18:00:00Z","endDate":"2021-09-08T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"AVE","startDate":"2021-07-05T18:00:00Z","endDate":"2021-07-05T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"AVE","startDate":"2021-07-22T09:00:00Z","endDate":"2021-07-22T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"AVE","startDate":"2021-09-15T13:00:00Z","endDate":"2021-09-15T16:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CDI","startDate":"2021-07-16T18:00:00Z","endDate":"2021-07-16T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"CDI","startDate":"2021-07-30T18:00:00Z","endDate":"2021-07-30T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"CDI","startDate":"2021-09-17T18:00:00Z","endDate":"2021-09-17T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CN","startDate":"2021-07-15T09:00:00Z","endDate":"2021-07-15T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"CN","startDate":"2021-07-29T18:00:00Z","endDate":"2021-07-29T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"CN","startDate":"2021-09-10T18:00:00Z","endDate":"2021-09-10T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"COM","startDate":"2021-07-12T18:00:00Z","endDate":"2021-07-12T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"COM","startDate":"2021-07-26T13:00:00Z","endDate":"2021-07-26T16:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"COM","startDate":"2021-09-13T18:00:00Z","endDate":"2021-09-13T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CQF","startDate":"2021-09-08T09:00:00Z","endDate":"2021-09-08T12:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CSM","startDate":"2021-07-12T18:00:00Z","endDate":"2021-07-12T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"CSM","startDate":"2021-07-31T09:00:00Z","endDate":"2021-07-31T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"CSM","startDate":"2021-09-08T09:00:00Z","endDate":"2021-09-08T12:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"DAW","startDate":"2021-06-28T18:00:00Z","endDate":"2021-06-28T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"DAW","startDate":"2021-07-21T09:00:00Z","endDate":"2021-07-21T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"DAW","startDate":"2021-09-15T18:00:00Z","endDate":"2021-09-15T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"EGP","startDate":"2021-07-01T18:00:00Z","endDate":"2021-07-01T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"EGP","startDate":"2021-07-20T13:00:00Z","endDate":"2021-07-20T16:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"EGP","startDate":"2021-09-09T18:00:00Z","endDate":"2021-09-09T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"Eltr","startDate":"2021-07-13T18:00:00Z","endDate":"2021-07-13T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"Eltr","startDate":"2021-07-27T09:00:00Z","endDate":"2021-07-27T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"Eltr","startDate":"2021-09-15T18:00:00Z","endDate":"2021-09-15T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"Emp","startDate":"2021-09-16T17:30:00Z","endDate":"2021-09-16T20:30:00Z","category":"EXAM_SPECIAL","location":""},{"course":"GAP","startDate":"2021-06-30T09:00:00Z","endDate":"2021-06-30T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"GAP","startDate":"2021-07-19T09:00:00Z","endDate":"2021-07-19T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"GAP","startDate":"2021-09-03T13:00:00Z","endDate":"2021-09-03T16:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"GQS","startDate":"2021-09-07T17:30:00Z","endDate":"2021-09-07T20:30:00Z","category":"EXAM_SPECIAL","location":""},{"course":"IASA","startDate":"2021-07-02T09:00:00Z","endDate":"2021-07-02T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"IASA","startDate":"2021-07-20T18:00:00Z","endDate":"2021-07-20T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"IASA","startDate":"2021-09-02T18:00:00Z","endDate":"2021-09-02T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"IEB","startDate":"2021-09-06T09:00:00Z","endDate":"2021-09-06T12:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"LC","startDate":"2021-09-14T09:00:00Z","endDate":"2021-09-14T12:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"LSD","startDate":"2021-07-06T09:00:00Z","endDate":"2021-07-06T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"LSD","startDate":"2021-07-21T18:00:00Z","endDate":"2021-07-21T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"LSD","startDate":"2021-09-06T18:00:00Z","endDate":"2021-09-06T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PC","startDate":"2021-07-13T18:00:00Z","endDate":"2021-07-13T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PC","startDate":"2021-07-27T18:00:00Z","endDate":"2021-07-27T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PC","startDate":"2021-09-17T18:00:00Z","endDate":"2021-09-17T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PDM","startDate":"2021-09-14T18:00:00Z","endDate":"2021-09-14T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PE","startDate":"2021-07-05T09:00:00Z","endDate":"2021-07-05T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PE","startDate":"2021-07-22T18:00:00Z","endDate":"2021-07-22T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PE","startDate":"2021-09-09T18:00:00Z","endDate":"2021-09-09T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PG","startDate":"2021-06-29T18:00:00Z","endDate":"2021-06-29T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PG","startDate":"2021-07-23T13:00:00Z","endDate":"2021-07-23T16:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PG","startDate":"2021-09-08T13:00:00Z","endDate":"2021-09-08T16:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PI","startDate":"2021-07-02T18:00:00Z","endDate":"2021-07-02T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PI","startDate":"2021-07-21T18:00:00Z","endDate":"2021-07-21T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PI","startDate":"2021-09-10T18:00:00Z","endDate":"2021-09-10T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"POO","startDate":"2021-09-14T18:00:00Z","endDate":"2021-09-14T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PSC","startDate":"2021-07-06T18:00:00Z","endDate":"2021-07-06T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PSC","startDate":"2021-07-21T13:00:00Z","endDate":"2021-07-21T16:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PSC","startDate":"2021-09-10T18:00:00Z","endDate":"2021-09-10T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"RCp","startDate":"2021-07-14T09:00:00Z","endDate":"2021-07-14T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"RCp","startDate":"2021-07-28T18:00:00Z","endDate":"2021-07-28T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"RCp","startDate":"2021-09-13T18:00:00Z","endDate":"2021-09-13T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"RI","startDate":"2021-09-07T13:00:00Z","endDate":"2021-09-07T16:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SE1","startDate":"2021-09-06T18:00:00Z","endDate":"2021-09-06T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SE2","startDate":"2021-07-09T18:00:00Z","endDate":"2021-07-09T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SE2","startDate":"2021-07-26T09:00:00Z","endDate":"2021-07-26T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SE2","startDate":"2021-09-06T18:00:00Z","endDate":"2021-09-06T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SG","startDate":"2021-07-01T09:00:00Z","endDate":"2021-07-01T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SG","startDate":"2021-07-20T18:00:00Z","endDate":"2021-07-20T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SG","startDate":"2021-09-08T13:00:00Z","endDate":"2021-09-08T16:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SegInf","startDate":"2021-09-17T13:00:00Z","endDate":"2021-09-17T16:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SI1","startDate":"2021-06-30T18:00:00Z","endDate":"2021-06-30T21:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SI1","startDate":"2021-07-19T13:00:00Z","endDate":"2021-07-19T16:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SI1","startDate":"2021-09-03T18:00:00Z","endDate":"2021-09-03T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SI2","startDate":"2021-07-08T13:00:00Z","endDate":"2021-07-08T16:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SI2","startDate":"2021-07-23T18:00:00Z","endDate":"2021-07-23T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SI2","startDate":"2021-09-13T09:00:00Z","endDate":"2021-09-13T12:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SM","startDate":"2021-09-15T09:00:00Z","endDate":"2021-09-15T12:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SO","startDate":"2021-07-09T09:00:00Z","endDate":"2021-07-09T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SO","startDate":"2021-07-26T18:00:00Z","endDate":"2021-07-26T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SO","startDate":"2021-09-07T18:00:00Z","endDate":"2021-09-07T21:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"TAR","startDate":"2021-07-16T09:00:00Z","endDate":"2021-07-16T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"TAR","startDate":"2021-07-30T18:00:00Z","endDate":"2021-07-30T21:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"TAR","startDate":"2021-09-07T09:00:00Z","endDate":"2021-09-07T12:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"TJ","startDate":"2021-07-12T09:00:00Z","endDate":"2021-07-12T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"TJ","startDate":"2021-07-28T09:00:00Z","endDate":"2021-07-28T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"TJ","startDate":"2021-09-17T13:00:00Z","endDate":"2021-09-17T16:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"TMD","startDate":"2021-07-10T09:00:00Z","endDate":"2021-07-10T12:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"TMD","startDate":"2021-07-29T09:00:00Z","endDate":"2021-07-29T12:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"TMD","startDate":"2021-09-13T13:00:00Z","endDate":"2021-09-13T16:00:00Z","category":"EXAM_SPECIAL","location":""}]}"""

        assertEquals(expectedJson, serialized)
    }
}
