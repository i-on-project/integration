package org.ionproject.integration.format.implementations

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ionproject.integration.model.external.calendar.AcademicCalendar
import org.ionproject.integration.model.external.calendar.AcademicCalendarDto
import org.ionproject.integration.model.external.calendar.EvaluationDto
import org.ionproject.integration.model.external.calendar.Event
import org.ionproject.integration.model.external.calendar.EventDto
import org.ionproject.integration.model.external.calendar.Term
import org.ionproject.integration.model.external.calendar.TermDto
import org.ionproject.integration.model.external.timetable.Language
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.external.timetable.SchoolDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month
import java.util.Date

internal class AcademicCalendarDtoFormatCheckerTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun `when Serialized Academic Calendar is equal to expected Dto then Success`() {

        val academicCalendar = AcademicCalendarDto(
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
                        ),
                        EventDto(
                            "Interrupção de atividades letivas (Carnaval)",
                            "2021-02-15",
                            "2021-02-16"
                        )
                    ),
                    listOf(
                        EvaluationDto(
                            "Período de exames (época normal)",
                            "2021-01-25",
                            "2021-02-13",
                            false
                        ),
                        EvaluationDto(
                            "Período de exames (época de recurso)",
                            "2021-02-17",
                            "2021-03-02",
                            false
                        ),
                        EvaluationDto(
                            "Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)",
                            "2021-03-09",
                            "2021-03-09",
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
                        ),
                        EventDto(
                            "Início das aulas",
                            "2020-10-06",
                            "2020-10-06"
                        ),
                        EventDto(
                            "Fim das aulas",
                            "2021-01-23",
                            "2021-01-23"
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
                        ),
                        EvaluationDto(
                            "Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)",
                            "2021-09-02",
                            "2021-09-02",
                            false
                        ),
                        EvaluationDto(
                            "Exames de época especial",
                            "2021-09-02",
                            "2021-09-18",
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
                        ),
                        EventDto(
                            "Fim das aulas",
                            "2021-06-26",
                            "2021-06-26"
                        ),
                        EventDto(
                            "Encerramento das atividades letivas 2020/2021",
                            "2021-07-31",
                            "2021-07-31"
                        ),
                        EventDto(
                            "Período de ausência de atividade letiva (férias)",
                            "2021-08-01",
                            "2021-08-31"
                        ),
                        EventDto(
                            "Data limite para lançamento de classificações no Portal Académico (época especial)",
                            "2021-09-30",
                            "2021-09-30"
                        ),
                        EventDto(
                            "Data limite para entrega de trabalhos finais de licenciatura*",
                            "2021-09-30",
                            "2021-09-30"
                        ),
                        EventDto(
                            "Data limite para entrega de trabalhos finais de mestrado",
                            "2021-09-30",
                            "2021-09-30"
                        ),
                        EventDto(
                            "Fim do ano letivo 2020/2021",
                            "2021-09-30",
                            "2021-09-30"
                        )
                    )
                )
            )
        )
        val serialized = mapper.writeValueAsString(academicCalendar)

        val json =
            """{"creationDateTime":"20200706T160021Z","retrievalDateTime":"20210606T235001Z","school":{"name":"Instituto Superior Engenharia Lisboa","acr":"ISEL"},"language":"pt-PT","terms":[{"calendarTerm":"2020-2021-1","interruptions":[{"name":"Interrupção de atividades letivas (Natal)","startDate":"2020-12-21","endDate":"2021-01-03"},{"name":"Interrupção de atividades letivas (Carnaval)","startDate":"2021-02-15","endDate":"2021-02-16"}],"evaluations":[{"name":"Período de exames (época normal)","startDate":"2021-01-25","endDate":"2021-02-13","duringLectures":false},{"name":"Período de exames (época de recurso)","startDate":"2021-02-17","endDate":"2021-03-02","duringLectures":false},{"name":"Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)","startDate":"2021-03-09","endDate":"2021-03-09","duringLectures":false}],"details":[],"otherEvents":[{"name":"Divulgação de horários","startDate":"2020-09-09","endDate":"2020-09-09"},{"name":"Abertura das atividades letivas 2020/2021","startDate":"2020-09-28","endDate":"2020-09-28"},{"name":"Início das aulas","startDate":"2020-10-06","endDate":"2020-10-06"},{"name":"Fim das aulas","startDate":"2021-01-23","endDate":"2021-01-23"}]},{"calendarTerm":"2020-2021-2","interruptions":[{"name":"Interrupção de atividades letivas (Páscoa)","startDate":"2021-03-29","endDate":"2021-04-05"}],"evaluations":[{"name":"Período de exames (época normal)","startDate":"2021-06-28","endDate":"2021-07-17","duringLectures":false},{"name":"Período de exames (época de recurso)","startDate":"2021-07-19","endDate":"2021-07-31","duringLectures":false},{"name":"Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)","startDate":"2021-09-02","endDate":"2021-09-02","duringLectures":false},{"name":"Exames de época especial","startDate":"2021-09-02","endDate":"2021-09-18","duringLectures":false}],"details":[],"otherEvents":[{"name":"Divulgação de horários","startDate":"2021-02-22","endDate":"2021-02-22"},{"name":"Início das aulas","startDate":"2021-03-15","endDate":"2021-03-15"},{"name":"Fim das aulas","startDate":"2021-06-26","endDate":"2021-06-26"},{"name":"Encerramento das atividades letivas 2020/2021","startDate":"2021-07-31","endDate":"2021-07-31"},{"name":"Período de ausência de atividade letiva (férias)","startDate":"2021-08-01","endDate":"2021-08-31"},{"name":"Data limite para lançamento de classificações no Portal Académico (época especial)","startDate":"2021-09-30","endDate":"2021-09-30"},{"name":"Data limite para entrega de trabalhos finais de licenciatura*","startDate":"2021-09-30","endDate":"2021-09-30"},{"name":"Data limite para entrega de trabalhos finais de mestrado","startDate":"2021-09-30","endDate":"2021-09-30"},{"name":"Fim do ano letivo 2020/2021","startDate":"2021-09-30","endDate":"2021-09-30"}]}]}"""

        assertEquals(json, serialized)
    }

    @Test
    fun `when Simple Business object is equal to expected Dto then Success`() {
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

        /*val academicCalendarBO = AcademicCalendar(
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


                                                    )
                    )
                )
            )
        )*/
    }
}
