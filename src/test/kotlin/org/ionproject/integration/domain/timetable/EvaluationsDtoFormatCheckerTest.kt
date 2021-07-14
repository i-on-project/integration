package org.ionproject.integration.domain.timetable

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.evaluations.Evaluations
import org.ionproject.integration.domain.evaluations.EvaluationsDto
import org.ionproject.integration.domain.evaluations.Exam
import org.ionproject.integration.domain.evaluations.ExamCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

internal class EvaluationsDtoFormatCheckerTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun `when Serialized Evaluations is equal to expected Dto then Success`() {

        val evaluations =
            Evaluations(
                ZonedDateTime.of(2021, 7, 8, 0, 12, 56, 0, ZoneId.systemDefault()),
                ZonedDateTime.of(2021, 7, 8, 0, 12, 56, 0, ZoneId.systemDefault()),
                School(
                    "Instituto Superior de Engenharia de Lisboa",
                    "ISEL"
                ),
                Programme(
                    "Licenciatura em Engenharia Informática e de Computadores",
                    "LEIC"
                ),
                "2020-2021-2",
                listOf(
                    Exam(
                        "AApl",
                        ZonedDateTime.of(2021, 7, 7, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 7, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AApl",
                        ZonedDateTime.of(2021, 7, 24, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 24, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AApl",
                        ZonedDateTime.of(2021, 9, 9, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 9, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "AC",
                        ZonedDateTime.of(2021, 7, 1, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 1, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AC",
                        ZonedDateTime.of(2021, 7, 19, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 19, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AC",
                        ZonedDateTime.of(2021, 9, 7, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 7, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "AED",
                        ZonedDateTime.of(2021, 6, 28, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 6, 28, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AED",
                        ZonedDateTime.of(2021, 7, 20, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 20, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AED",
                        ZonedDateTime.of(2021, 9, 16, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 16, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "ALGA",
                        ZonedDateTime.of(2021, 7, 8, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 8, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "ALGA",
                        ZonedDateTime.of(2021, 7, 23, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 23, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "ALGA",
                        ZonedDateTime.of(2021, 9, 8, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 8, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "AVE",
                        ZonedDateTime.of(2021, 7, 5, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 5, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AVE",
                        ZonedDateTime.of(2021, 7, 22, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 22, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AVE",
                        ZonedDateTime.of(2021, 9, 15, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 15, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CDI",
                        ZonedDateTime.of(2021, 7, 16, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 16, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "CDI",
                        ZonedDateTime.of(2021, 7, 30, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 30, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "CDI",
                        ZonedDateTime.of(2021, 9, 17, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 17, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CN",
                        ZonedDateTime.of(2021, 7, 15, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 15, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "CN",
                        ZonedDateTime.of(2021, 7, 29, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 29, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "CN",
                        ZonedDateTime.of(2021, 9, 10, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 10, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "COM",
                        ZonedDateTime.of(2021, 7, 12, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 12, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "COM",
                        ZonedDateTime.of(2021, 7, 26, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 26, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "COM",
                        ZonedDateTime.of(2021, 9, 13, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 13, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CQF",
                        ZonedDateTime.of(2021, 9, 8, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 8, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CSM",
                        ZonedDateTime.of(2021, 7, 12, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 12, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "CSM",
                        ZonedDateTime.of(2021, 7, 31, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 31, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "CSM",
                        ZonedDateTime.of(2021, 9, 8, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 8, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "DAW",
                        ZonedDateTime.of(2021, 6, 28, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 6, 28, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "DAW",
                        ZonedDateTime.of(2021, 7, 21, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 21, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "DAW",
                        ZonedDateTime.of(2021, 9, 15, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 15, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "EGP",
                        ZonedDateTime.of(2021, 7, 1, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 1, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "EGP",
                        ZonedDateTime.of(2021, 7, 20, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 20, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "EGP",
                        ZonedDateTime.of(2021, 9, 9, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 9, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "Eltr",
                        ZonedDateTime.of(2021, 7, 13, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 13, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "Eltr",
                        ZonedDateTime.of(2021, 7, 27, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 27, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "Eltr",
                        ZonedDateTime.of(2021, 9, 15, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 15, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "Emp",
                        ZonedDateTime.of(2021, 9, 16, 18, 30, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 16, 21, 30, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "GAP",
                        ZonedDateTime.of(2021, 6, 30, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 6, 30, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "GAP",
                        ZonedDateTime.of(2021, 7, 19, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 19, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "GAP",
                        ZonedDateTime.of(2021, 9, 3, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 3, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "GQS",
                        ZonedDateTime.of(2021, 9, 7, 18, 30, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 7, 21, 30, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "IASA",
                        ZonedDateTime.of(2021, 7, 2, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 2, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "IASA",
                        ZonedDateTime.of(2021, 7, 20, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 20, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "IASA",
                        ZonedDateTime.of(2021, 9, 2, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 2, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "IEB",
                        ZonedDateTime.of(2021, 9, 6, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 6, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "LC",
                        ZonedDateTime.of(2021, 9, 14, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 14, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "LSD",
                        ZonedDateTime.of(2021, 7, 6, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 6, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "LSD",
                        ZonedDateTime.of(2021, 7, 21, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 21, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "LSD",
                        ZonedDateTime.of(2021, 9, 6, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 6, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PC",
                        ZonedDateTime.of(2021, 7, 13, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 13, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PC",
                        ZonedDateTime.of(2021, 7, 27, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 27, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PC",
                        ZonedDateTime.of(2021, 9, 17, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 17, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PDM",
                        ZonedDateTime.of(2021, 9, 14, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 14, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PE",
                        ZonedDateTime.of(2021, 7, 5, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 5, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PE",
                        ZonedDateTime.of(2021, 7, 22, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 22, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PE",
                        ZonedDateTime.of(2021, 9, 9, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 9, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PG",
                        ZonedDateTime.of(2021, 6, 29, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 6, 29, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PG",
                        ZonedDateTime.of(2021, 7, 23, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 23, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PG",
                        ZonedDateTime.of(2021, 9, 8, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 8, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PI",
                        ZonedDateTime.of(2021, 7, 2, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 2, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PI",
                        ZonedDateTime.of(2021, 7, 21, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 21, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PI",
                        ZonedDateTime.of(2021, 9, 10, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 10, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "POO",
                        ZonedDateTime.of(2021, 9, 14, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 14, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PSC",
                        ZonedDateTime.of(2021, 7, 6, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 6, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PSC",
                        ZonedDateTime.of(2021, 7, 21, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 21, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PSC",
                        ZonedDateTime.of(2021, 9, 10, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 10, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "RCp",
                        ZonedDateTime.of(2021, 7, 14, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 14, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "RCp",
                        ZonedDateTime.of(2021, 7, 28, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 28, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "RCp",
                        ZonedDateTime.of(2021, 9, 13, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 13, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "RI",
                        ZonedDateTime.of(2021, 9, 7, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 7, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SE1",
                        ZonedDateTime.of(2021, 9, 6, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 6, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SE2",
                        ZonedDateTime.of(2021, 7, 9, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 9, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SE2",
                        ZonedDateTime.of(2021, 7, 26, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 26, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SE2",
                        ZonedDateTime.of(2021, 9, 6, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 6, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SG",
                        ZonedDateTime.of(2021, 7, 1, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 1, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SG",
                        ZonedDateTime.of(2021, 7, 20, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 20, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SG",
                        ZonedDateTime.of(2021, 9, 8, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 8, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SegInf",
                        ZonedDateTime.of(2021, 9, 17, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 17, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SI1",
                        ZonedDateTime.of(2021, 6, 30, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 6, 30, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SI1",
                        ZonedDateTime.of(2021, 7, 19, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 19, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SI1",
                        ZonedDateTime.of(2021, 9, 3, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 3, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SI2",
                        ZonedDateTime.of(2021, 7, 8, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 8, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SI2",
                        ZonedDateTime.of(2021, 7, 23, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 23, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SI2",
                        ZonedDateTime.of(2021, 9, 13, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 13, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SM",
                        ZonedDateTime.of(2021, 9, 15, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 15, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SO",
                        ZonedDateTime.of(2021, 7, 9, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 9, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SO",
                        ZonedDateTime.of(2021, 7, 26, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 26, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SO",
                        ZonedDateTime.of(2021, 9, 7, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 7, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "TAR",
                        ZonedDateTime.of(2021, 7, 16, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 16, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "TAR",
                        ZonedDateTime.of(2021, 7, 30, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 30, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "TAR",
                        ZonedDateTime.of(2021, 9, 7, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 7, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "TJ",
                        ZonedDateTime.of(2021, 7, 12, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 12, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "TJ",
                        ZonedDateTime.of(2021, 7, 28, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 28, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "TJ",
                        ZonedDateTime.of(2021, 9, 17, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 17, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "TMD",
                        ZonedDateTime.of(2021, 7, 10, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 10, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "TMD",
                        ZonedDateTime.of(2021, 7, 29, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 29, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "TMD",
                        ZonedDateTime.of(2021, 9, 13, 14, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 9, 13, 17, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    )
                )
            )
        val serialized = mapper.writeValueAsString(EvaluationsDto.from(evaluations))

        val expectedJson =
            """{"creationDateTime":"2021-07-08T00:12:56Z","retrievalDateTime":"2021-07-08T00:12:56Z","school":{"name":"Instituto Superior de Engenharia de Lisboa","acr":"ISEL"},"programme":{"name":"Licenciatura em Engenharia Informática e de Computadores","acr":"LEIC"},"calendarTerm":"2020-2021-2","exams":[{"course":"AApl","startDate":"2021-07-07T10:00:00Z","endDate":"2021-07-07T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"AApl","startDate":"2021-07-24T10:00:00Z","endDate":"2021-07-24T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"AApl","startDate":"2021-09-09T14:00:00Z","endDate":"2021-09-09T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"AC","startDate":"2021-07-01T14:00:00Z","endDate":"2021-07-01T17:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"AC","startDate":"2021-07-19T19:00:00Z","endDate":"2021-07-19T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"AC","startDate":"2021-09-07T10:00:00Z","endDate":"2021-09-07T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"AED","startDate":"2021-06-28T19:00:00Z","endDate":"2021-06-28T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"AED","startDate":"2021-07-20T14:00:00Z","endDate":"2021-07-20T17:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"AED","startDate":"2021-09-16T19:00:00Z","endDate":"2021-09-16T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"ALGA","startDate":"2021-07-08T19:00:00Z","endDate":"2021-07-08T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"ALGA","startDate":"2021-07-23T10:00:00Z","endDate":"2021-07-23T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"ALGA","startDate":"2021-09-08T19:00:00Z","endDate":"2021-09-08T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"AVE","startDate":"2021-07-05T19:00:00Z","endDate":"2021-07-05T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"AVE","startDate":"2021-07-22T10:00:00Z","endDate":"2021-07-22T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"AVE","startDate":"2021-09-15T14:00:00Z","endDate":"2021-09-15T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CDI","startDate":"2021-07-16T19:00:00Z","endDate":"2021-07-16T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"CDI","startDate":"2021-07-30T19:00:00Z","endDate":"2021-07-30T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"CDI","startDate":"2021-09-17T19:00:00Z","endDate":"2021-09-17T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CN","startDate":"2021-07-15T10:00:00Z","endDate":"2021-07-15T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"CN","startDate":"2021-07-29T19:00:00Z","endDate":"2021-07-29T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"CN","startDate":"2021-09-10T19:00:00Z","endDate":"2021-09-10T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"COM","startDate":"2021-07-12T19:00:00Z","endDate":"2021-07-12T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"COM","startDate":"2021-07-26T14:00:00Z","endDate":"2021-07-26T17:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"COM","startDate":"2021-09-13T19:00:00Z","endDate":"2021-09-13T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CQF","startDate":"2021-09-08T10:00:00Z","endDate":"2021-09-08T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CSM","startDate":"2021-07-12T19:00:00Z","endDate":"2021-07-12T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"CSM","startDate":"2021-07-31T10:00:00Z","endDate":"2021-07-31T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"CSM","startDate":"2021-09-08T10:00:00Z","endDate":"2021-09-08T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"DAW","startDate":"2021-06-28T19:00:00Z","endDate":"2021-06-28T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"DAW","startDate":"2021-07-21T10:00:00Z","endDate":"2021-07-21T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"DAW","startDate":"2021-09-15T19:00:00Z","endDate":"2021-09-15T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"EGP","startDate":"2021-07-01T19:00:00Z","endDate":"2021-07-01T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"EGP","startDate":"2021-07-20T14:00:00Z","endDate":"2021-07-20T17:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"EGP","startDate":"2021-09-09T19:00:00Z","endDate":"2021-09-09T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"Eltr","startDate":"2021-07-13T19:00:00Z","endDate":"2021-07-13T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"Eltr","startDate":"2021-07-27T10:00:00Z","endDate":"2021-07-27T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"Eltr","startDate":"2021-09-15T19:00:00Z","endDate":"2021-09-15T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"Emp","startDate":"2021-09-16T18:30:00Z","endDate":"2021-09-16T21:30:00Z","category":"EXAM_SPECIAL","location":""},{"course":"GAP","startDate":"2021-06-30T10:00:00Z","endDate":"2021-06-30T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"GAP","startDate":"2021-07-19T10:00:00Z","endDate":"2021-07-19T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"GAP","startDate":"2021-09-03T14:00:00Z","endDate":"2021-09-03T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"GQS","startDate":"2021-09-07T18:30:00Z","endDate":"2021-09-07T21:30:00Z","category":"EXAM_SPECIAL","location":""},{"course":"IASA","startDate":"2021-07-02T10:00:00Z","endDate":"2021-07-02T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"IASA","startDate":"2021-07-20T19:00:00Z","endDate":"2021-07-20T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"IASA","startDate":"2021-09-02T19:00:00Z","endDate":"2021-09-02T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"IEB","startDate":"2021-09-06T10:00:00Z","endDate":"2021-09-06T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"LC","startDate":"2021-09-14T10:00:00Z","endDate":"2021-09-14T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"LSD","startDate":"2021-07-06T10:00:00Z","endDate":"2021-07-06T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"LSD","startDate":"2021-07-21T19:00:00Z","endDate":"2021-07-21T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"LSD","startDate":"2021-09-06T19:00:00Z","endDate":"2021-09-06T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PC","startDate":"2021-07-13T19:00:00Z","endDate":"2021-07-13T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PC","startDate":"2021-07-27T19:00:00Z","endDate":"2021-07-27T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PC","startDate":"2021-09-17T19:00:00Z","endDate":"2021-09-17T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PDM","startDate":"2021-09-14T19:00:00Z","endDate":"2021-09-14T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PE","startDate":"2021-07-05T10:00:00Z","endDate":"2021-07-05T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PE","startDate":"2021-07-22T19:00:00Z","endDate":"2021-07-22T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PE","startDate":"2021-09-09T19:00:00Z","endDate":"2021-09-09T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PG","startDate":"2021-06-29T19:00:00Z","endDate":"2021-06-29T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PG","startDate":"2021-07-23T14:00:00Z","endDate":"2021-07-23T17:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PG","startDate":"2021-09-08T14:00:00Z","endDate":"2021-09-08T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PI","startDate":"2021-07-02T19:00:00Z","endDate":"2021-07-02T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PI","startDate":"2021-07-21T19:00:00Z","endDate":"2021-07-21T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PI","startDate":"2021-09-10T19:00:00Z","endDate":"2021-09-10T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"POO","startDate":"2021-09-14T19:00:00Z","endDate":"2021-09-14T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PSC","startDate":"2021-07-06T19:00:00Z","endDate":"2021-07-06T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PSC","startDate":"2021-07-21T14:00:00Z","endDate":"2021-07-21T17:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PSC","startDate":"2021-09-10T19:00:00Z","endDate":"2021-09-10T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"RCp","startDate":"2021-07-14T10:00:00Z","endDate":"2021-07-14T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"RCp","startDate":"2021-07-28T19:00:00Z","endDate":"2021-07-28T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"RCp","startDate":"2021-09-13T19:00:00Z","endDate":"2021-09-13T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"RI","startDate":"2021-09-07T14:00:00Z","endDate":"2021-09-07T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SE1","startDate":"2021-09-06T19:00:00Z","endDate":"2021-09-06T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SE2","startDate":"2021-07-09T19:00:00Z","endDate":"2021-07-09T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SE2","startDate":"2021-07-26T10:00:00Z","endDate":"2021-07-26T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SE2","startDate":"2021-09-06T19:00:00Z","endDate":"2021-09-06T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SG","startDate":"2021-07-01T10:00:00Z","endDate":"2021-07-01T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SG","startDate":"2021-07-20T19:00:00Z","endDate":"2021-07-20T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SG","startDate":"2021-09-08T14:00:00Z","endDate":"2021-09-08T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SegInf","startDate":"2021-09-17T14:00:00Z","endDate":"2021-09-17T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SI1","startDate":"2021-06-30T19:00:00Z","endDate":"2021-06-30T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SI1","startDate":"2021-07-19T14:00:00Z","endDate":"2021-07-19T17:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SI1","startDate":"2021-09-03T19:00:00Z","endDate":"2021-09-03T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SI2","startDate":"2021-07-08T14:00:00Z","endDate":"2021-07-08T17:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SI2","startDate":"2021-07-23T19:00:00Z","endDate":"2021-07-23T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SI2","startDate":"2021-09-13T10:00:00Z","endDate":"2021-09-13T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SM","startDate":"2021-09-15T10:00:00Z","endDate":"2021-09-15T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SO","startDate":"2021-07-09T10:00:00Z","endDate":"2021-07-09T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SO","startDate":"2021-07-26T19:00:00Z","endDate":"2021-07-26T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SO","startDate":"2021-09-07T19:00:00Z","endDate":"2021-09-07T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"TAR","startDate":"2021-07-16T10:00:00Z","endDate":"2021-07-16T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"TAR","startDate":"2021-07-30T19:00:00Z","endDate":"2021-07-30T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"TAR","startDate":"2021-09-07T10:00:00Z","endDate":"2021-09-07T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"TJ","startDate":"2021-07-12T10:00:00Z","endDate":"2021-07-12T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"TJ","startDate":"2021-07-28T10:00:00Z","endDate":"2021-07-28T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"TJ","startDate":"2021-09-17T14:00:00Z","endDate":"2021-09-17T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"TMD","startDate":"2021-07-10T10:00:00Z","endDate":"2021-07-10T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"TMD","startDate":"2021-07-29T10:00:00Z","endDate":"2021-07-29T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"TMD","startDate":"2021-09-13T14:00:00Z","endDate":"2021-09-13T17:00:00Z","category":"EXAM_SPECIAL","location":""}]}"""

        assertEquals(expectedJson, serialized)
    }
}
