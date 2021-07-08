package org.ionproject.integration.format.implementations

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ionproject.integration.domain.calendar.AcademicCalendarDto
import org.ionproject.integration.domain.calendar.EvaluationDto
import org.ionproject.integration.domain.calendar.EventDto
import org.ionproject.integration.domain.calendar.TermDto
import org.ionproject.integration.domain.common.Language
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.common.dto.ProgrammeDto
import org.ionproject.integration.domain.common.dto.SchoolDto
import org.ionproject.integration.domain.evaluations.EvaluationsDto
import org.ionproject.integration.domain.evaluations.ExamDto
import org.ionproject.integration.model.external.calendar.AcademicCalendar
import org.ionproject.integration.model.external.calendar.Evaluation
import org.ionproject.integration.model.external.calendar.Event
import org.ionproject.integration.model.external.calendar.Term
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month

internal class EvaluationsDtoFormatCheckerTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun `when Serialized Evaluations is equal to expected Dto then Success`() {

        val evaluations = EvaluationsDto(
            "2021-07-08T00:12:56Z",
            "2021-07-08T00:12:56Z",
            SchoolDto("Instituto Superior Engenharia Lisboa", "ISEL"),
            ProgrammeDto("Mestrado em Engenharia Informática e de Computadores", "MEIC"),
            "2020-2021-2",
            listOf(
                ExamDto(
                    "AMD",
                    "2021-09-13T19:00:00Z",
                    "2021-09-13T19:00:00Z",
                    "EXAM_SPECIAL",
                    ""
                ),
                ExamDto(
                    "ASI",
                    "2021-09-15T19:00:00Z",
                    "2021-09-15T22:00:00Z",
                    "EXAM_SPECIAL",
                    ""
                ),
                ExamDto(
                    "AMD",
                    "2021-09-13T19:00:00Z",
                    "2021-09-13T19:00:00Z",
                    "EXAM_SPECIAL",
                    ""
                ),
                ExamDto(
                    "AMD",
                    "2021-09-13T19:00:00Z",
                    "2021-09-13T19:00:00Z",
                    "EXAM_SPECIAL",
                    ""
                ),
                ExamDto(
                    "AMD",
                    "2021-09-13T19:00:00Z",
                    "2021-09-13T19:00:00Z",
                    "EXAM_SPECIAL",
                    ""
                ),
                ExamDto(
                    "AMD",
                    "2021-09-13T19:00:00Z",
                    "2021-09-13T19:00:00Z",
                    "EXAM_SPECIAL",
                    ""
                )
            )
        )
        val serialized = mapper.writeValueAsString(evaluations)

        val json =
            """{"creationDateTime":"2021-07-08T00:12:56Z","retrievalDateTime":"2021-07-08T00:12:56Z","school":{"name":"Instituto Superior de Engenharia de Lisboa","acr":"ISEL"},"programme":{"name":"Mestrado em Engenharia Informática e de Computadores","acr":"MEIC"},"calendarTerm":"2020-2021-2","exams":[{"course":"AMD","startDate":"2021-09-13T19:00:00Z","endDate":"2021-09-13T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"ASI","startDate":"2021-09-15T19:00:00Z","endDate":"2021-09-15T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CCD","startDate":"2021-09-17T19:00:00Z","endDate":"2021-09-17T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CD","startDate":"2021-09-15T19:00:00Z","endDate":"2021-09-15T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CDLE","startDate":"2021-09-06T19:00:00Z","endDate":"2021-09-06T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CS","startDate":"2021-09-06T19:00:00Z","endDate":"2021-09-06T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"CSI","startDate":"2021-09-10T19:00:00Z","endDate":"2021-09-10T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"DI3D","startDate":"2021-09-14T19:00:00Z","endDate":"2021-09-14T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"EGP","startDate":"2021-07-01T19:00:00Z","endDate":"2021-07-01T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"EGP","startDate":"2021-07-20T14:00:00Z","endDate":"2021-07-20T17:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"EGP","startDate":"2021-09-09T19:00:00Z","endDate":"2021-09-09T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"ES","startDate":"2021-09-04T10:00:00Z","endDate":"2021-09-04T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"GSI","startDate":"2021-07-07T14:00:00Z","endDate":"2021-07-07T17:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"GSI","startDate":"2021-07-22T19:00:00Z","endDate":"2021-07-22T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"GSI","startDate":"2021-09-02T19:00:00Z","endDate":"2021-09-02T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"IESD","startDate":"2021-06-29T19:00:00Z","endDate":"2021-06-29T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"IESD","startDate":"2021-07-20T10:00:00Z","endDate":"2021-07-20T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"IESD","startDate":"2021-09-14T19:00:00Z","endDate":"2021-09-14T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"IoT","startDate":"2021-07-15T10:00:00Z","endDate":"2021-07-15T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"IoT","startDate":"2021-07-28T19:00:00Z","endDate":"2021-07-28T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"IoT","startDate":"2021-09-03T14:00:00Z","endDate":"2021-09-03T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"IRS","startDate":"2021-09-08T19:00:00Z","endDate":"2021-09-08T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"MDLE","startDate":"2021-07-13T14:00:00Z","endDate":"2021-07-13T17:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"MDLE","startDate":"2021-07-19T14:00:00Z","endDate":"2021-07-19T17:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"MDLE","startDate":"2021-09-07T14:00:00Z","endDate":"2021-09-07T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"PIB","startDate":"2021-07-16T19:00:00Z","endDate":"2021-07-16T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"PIB","startDate":"2021-07-26T10:00:00Z","endDate":"2021-07-26T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"PIB","startDate":"2021-09-15T19:00:00Z","endDate":"2021-09-15T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"RCMov","startDate":"2021-07-12T19:00:00Z","endDate":"2021-07-12T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"RCMov","startDate":"2021-07-27T10:00:00Z","endDate":"2021-07-27T13:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"RCMov","startDate":"2021-09-11T10:00:00Z","endDate":"2021-09-11T13:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"RDC","startDate":"2021-09-08T19:00:00Z","endDate":"2021-09-08T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"RPC","startDate":"2021-07-01T19:00:00Z","endDate":"2021-07-01T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"RPC","startDate":"2021-07-29T19:00:00Z","endDate":"2021-07-29T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"RPC","startDate":"2021-09-17T14:00:00Z","endDate":"2021-09-17T17:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SIAD","startDate":"2021-07-05T19:00:00Z","endDate":"2021-07-05T22:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SIAD","startDate":"2021-07-21T14:00:00Z","endDate":"2021-07-21T17:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SIAD","startDate":"2021-09-08T19:00:00Z","endDate":"2021-09-08T22:00:00Z","category":"EXAM_SPECIAL","location":""},{"course":"SRC","startDate":"2021-07-09T10:00:00Z","endDate":"2021-07-09T13:00:00Z","category":"EXAM_NORMAL","location":""},{"course":"SRC","startDate":"2021-07-23T19:00:00Z","endDate":"2021-07-23T22:00:00Z","category":"EXAM_ALTERN","location":""},{"course":"SRC","startDate":"2021-09-13T10:00:00Z","endDate":"2021-09-13T13:00:00Z","category":"EXAM_SPECIAL","location":""}]}"""

        assertEquals(json, serialized)
    }

    @Test
    fun `when Business object is equal to expected Dto then Success`() {
        val academicCalendarDto = AcademicCalendarDto(
            "20200706T160021Z",
            "20210606T235001Z",
            SchoolDto("Instituto Superior Engenharia Lisboa", "ISEL"),
            "pt-PT",
            listOf(
                TermDto(
                    "2020-2021-1",
                    listOf(
                        EventDto(
                            "Interrupção de atividades letivas (Natal)",
                            "2020-12-21",
                            "2021-01-03"
                        )
                    ),
                    listOf(
                        EvaluationDto(
                            "Período de exames (época normal)",
                            "2021-01-25",
                            "2021-02-13",
                            false
                        )
                    ),
                    listOf(),
                    listOf(
                        EventDto(
                            "Divulgação de horários",
                            "2020-09-09",
                            "2020-09-09"
                        ),
                        EventDto(
                            "Abertura das atividades letivas 2020/2021",
                            "2020-09-28",
                            "2020-09-28"
                        )
                    )
                ),
                TermDto(
                    "2020-2021-2",
                    listOf(
                        EventDto(
                            "Interrupção de atividades letivas (Páscoa)",
                            "2021-03-29",
                            "2021-04-05"
                        )
                    ),
                    listOf(
                        EvaluationDto(
                            "Período de exames (época normal)",
                            "2021-06-28",
                            "2021-07-17",
                            false
                        ),
                        EvaluationDto(
                            "Período de exames (época de recurso)",
                            "2021-07-19",
                            "2021-07-31",
                            false
                        )
                    ),
                    listOf(),
                    listOf(
                        EventDto(
                            "Divulgação de horários",
                            "2021-02-22",
                            "2021-02-22"
                        ),
                        EventDto(
                            "Início das aulas",
                            "2021-03-15",
                            "2021-03-15"
                        )
                    )
                )
            )
        )

        val academicCalendarBO = AcademicCalendar(
            "20200706T160021Z",
            "20210606T235001Z",
            School("Instituto Superior Engenharia Lisboa", "ISEL"),
            Language.PT,
            listOf(
                Term(
                    "2020-2021-1",
                    listOf(
                        Event(
                            "Interrupção de atividades letivas (Natal)",
                            LocalDate.of(2020, Month.DECEMBER, 21),
                            LocalDate.of(2021, Month.JANUARY, 3)
                        )
                    ),
                    listOf(
                        Evaluation(
                            "Período de exames (época normal)",
                            LocalDate.of(2021, Month.JANUARY, 25),
                            LocalDate.of(2021, Month.FEBRUARY, 13),
                            false
                        )
                    ),
                    listOf(),
                    listOf(
                        Event(
                            "Divulgação de horários",
                            LocalDate.of(2020, Month.SEPTEMBER, 9),
                            LocalDate.of(2020, Month.SEPTEMBER, 9)
                        ),
                        Event(
                            "Abertura das atividades letivas 2020/2021",
                            LocalDate.of(2020, Month.SEPTEMBER, 28),
                            LocalDate.of(2020, Month.SEPTEMBER, 28)
                        )
                    )
                ),
                Term(
                    "2020-2021-2",
                    listOf(
                        Event(
                            "Interrupção de atividades letivas (Páscoa)",
                            LocalDate.of(2021, Month.MARCH, 29),
                            LocalDate.of(2021, Month.APRIL, 5)
                        )
                    ),
                    listOf(
                        Evaluation(
                            "Período de exames (época normal)",
                            LocalDate.of(2021, Month.JUNE, 28),
                            LocalDate.of(2021, Month.JULY, 17),
                            false
                        ),
                        Evaluation(
                            "Período de exames (época de recurso)",
                            LocalDate.of(2021, Month.JULY, 19),
                            LocalDate.of(2021, Month.JULY, 31),
                            false
                        )
                    ),
                    listOf(),
                    listOf(
                        Event(
                            "Divulgação de horários",
                            LocalDate.of(2021, Month.FEBRUARY, 22),
                            LocalDate.of(2021, Month.FEBRUARY, 22)
                        ),
                        Event(
                            "Início das aulas",
                            LocalDate.of(2021, Month.MARCH, 15),
                            LocalDate.of(2021, Month.MARCH, 15)
                        )
                    )
                )
            )
        )
        assertEquals(AcademicCalendarDto.from(academicCalendarBO), academicCalendarDto)
    }
}
