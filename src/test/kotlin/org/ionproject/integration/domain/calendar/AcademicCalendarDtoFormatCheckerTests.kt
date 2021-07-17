package org.ionproject.integration.domain.calendar

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dispatcher.IDispatcher
import org.ionproject.integration.application.job.ISELAcademicCalendarJob
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.dto.SchoolDto
import org.ionproject.integration.infrastructure.DateUtils
import org.ionproject.integration.infrastructure.http.IFileDownloader
import org.ionproject.integration.infrastructure.repository.model.IInstitutionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import java.io.File
import java.net.URI
import javax.sql.DataSource

internal class AcademicCalendarDtoFormatCheckerTests {
    private val mockJobBuilderFactory = mock<JobBuilderFactory> {}

    private val mockStepBuilderFactory = mock<StepBuilderFactory> {}

    private val mockAppProperties = mock<AppProperties> {}

    private val mockDownloader = mock<IFileDownloader> {}

    private val mockDispatcher = mock<IDispatcher> {}

    private val mockInstitution = mock<IInstitutionRepository> {}

    private val mockDataSource = mock<DataSource> {}

    private val mapper = jacksonObjectMapper()

    @Test
    fun `when Serialized Academic Calendar is equal to expected Dto then Success`() {

        val academicCalendar = AcademicCalendarDto(
            "2020-07-06T16:00:21Z",
            "2021-06-06T23:50:01Z",
            SchoolDto("Instituto Superior Engenharia Lisboa", "ISEL"),
            "pt-PT",
            listOf(
                TermEventsDto(
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
                        )
                    ),
                    listOf(
                        LecturesDto(
                            "Aulas",
                            listOf(IdDto(1), IdDto(2), IdDto(3), IdDto(4), IdDto(5), IdDto(6)),
                            "2020-10-06",
                            "2021-01-23"
                        )
                    ),
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
                            "Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)",
                            "2021-03-09",
                            "2021-03-09"
                        )
                    )
                ),
                TermEventsDto(
                    "2020-2021-2",
                    listOf(
                        EventDto(
                            "Interrupção de atividades letivas (Páscoa)",
                            "2021-03-29",
                            "2021-04-05"
                        ),
                        EventDto(
                            "Período de ausência de atividade letiva (férias)",
                            "2021-08-01",
                            "2021-08-31"
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
                            "Exames de época especial",
                            "2021-09-02",
                            "2021-09-18",
                            false
                        )
                    ),
                    listOf(
                        LecturesDto(
                            "Aulas",
                            listOf(IdDto(1), IdDto(2), IdDto(3), IdDto(4), IdDto(5), IdDto(6)),
                            "2021-03-15",
                            "2021-06-26"
                        )
                    ),
                    listOf(
                        EventDto(
                            "Divulgação de horários",
                            "2021-02-22",
                            "2021-02-22"
                        ),
                        EventDto(
                            "Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)",
                            "2021-09-02",
                            "2021-09-02",
                        ),
                        EventDto(
                            "Encerramento das atividades letivas 2020/2021",
                            "2021-07-31",
                            "2021-07-31"
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
            """{"creationDateTime":"2020-07-06T16:00:21Z","retrievalDateTime":"2021-06-06T23:50:01Z","school":{"name":"Instituto Superior Engenharia Lisboa","acr":"ISEL"},"language":"pt-PT","terms":[{"calendarTerm":"2020-2021-1","interruptions":[{"name":"Interrupção de atividades letivas (Natal)","startDate":"2020-12-21","endDate":"2021-01-03"},{"name":"Interrupção de atividades letivas (Carnaval)","startDate":"2021-02-15","endDate":"2021-02-16"}],"evaluations":[{"name":"Período de exames (época normal)","startDate":"2021-01-25","endDate":"2021-02-13","duringLectures":false},{"name":"Período de exames (época de recurso)","startDate":"2021-02-17","endDate":"2021-03-02","duringLectures":false}],"lectures":[{"name":"Aulas","curricularTerm":[{"id":1},{"id":2},{"id":3},{"id":4},{"id":5},{"id":6}],"startDate":"2020-10-06","endDate":"2021-01-23"}],"otherEvents":[{"name":"Divulgação de horários","startDate":"2020-09-09","endDate":"2020-09-09"},{"name":"Abertura das atividades letivas 2020/2021","startDate":"2020-09-28","endDate":"2020-09-28"},{"name":"Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)","startDate":"2021-03-09","endDate":"2021-03-09"}]},{"calendarTerm":"2020-2021-2","interruptions":[{"name":"Interrupção de atividades letivas (Páscoa)","startDate":"2021-03-29","endDate":"2021-04-05"},{"name":"Período de ausência de atividade letiva (férias)","startDate":"2021-08-01","endDate":"2021-08-31"}],"evaluations":[{"name":"Período de exames (época normal)","startDate":"2021-06-28","endDate":"2021-07-17","duringLectures":false},{"name":"Período de exames (época de recurso)","startDate":"2021-07-19","endDate":"2021-07-31","duringLectures":false},{"name":"Exames de época especial","startDate":"2021-09-02","endDate":"2021-09-18","duringLectures":false}],"lectures":[{"name":"Aulas","curricularTerm":[{"id":1},{"id":2},{"id":3},{"id":4},{"id":5},{"id":6}],"startDate":"2021-03-15","endDate":"2021-06-26"}],"otherEvents":[{"name":"Divulgação de horários","startDate":"2021-02-22","endDate":"2021-02-22"},{"name":"Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)","startDate":"2021-09-02","endDate":"2021-09-02"},{"name":"Encerramento das atividades letivas 2020/2021","startDate":"2021-07-31","endDate":"2021-07-31"},{"name":"Data limite para lançamento de classificações no Portal Académico (época especial)","startDate":"2021-09-30","endDate":"2021-09-30"},{"name":"Data limite para entrega de trabalhos finais de licenciatura*","startDate":"2021-09-30","endDate":"2021-09-30"},{"name":"Data limite para entrega de trabalhos finais de mestrado","startDate":"2021-09-30","endDate":"2021-09-30"},{"name":"Fim do ano letivo 2020/2021","startDate":"2021-09-30","endDate":"2021-09-30"}]}]}"""

        assertEquals(json, serialized)
    }

    @Test
    fun `when Business object is equal to expected Dto then Success`() {
        val resourceFile = File("src/test/resources/calendarTest.pdf")

        val job = ISELAcademicCalendarJob(
            mockJobBuilderFactory,
            mockStepBuilderFactory,
            mockAppProperties,
            mockDownloader,
            mockDispatcher,
            mockInstitution,
            mockDataSource
        )
        val calendarData = job.extractCalendarPDF(resourceFile.toPath().toString())

        val institution = InstitutionModel(
            "Instituto Superior de Engenharia de Lisboa",
            "ISEL",
            "pt.ipl.isel",
            "Europe/Lisbon",
            URI("")
        )

        val academicCalendar = AcademicCalendar.from(calendarData, institution)

        val academicCalendarRetrievedDTO = AcademicCalendarDto.from(academicCalendar)

        val academicCalendarExpectedDto = AcademicCalendarDto(
            "2020-07-06T16:00:21Z",
            DateUtils.formatToISO8601(academicCalendar.retrievalDateTime),
            SchoolDto("Instituto Superior de Engenharia de Lisboa", "ISEL"),
            "pt-PT",
            listOf(
                TermEventsDto(
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
                        )
                    ),
                    listOf(
                        LecturesDto(
                            "Todas as turmas",
                            listOf(IdDto(1), IdDto(2), IdDto(3), IdDto(4), IdDto(5), IdDto(6)),
                            "2020-10-06",
                            "2021-01-23"
                        )
                    ),
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
                            "Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)",
                            "2021-03-09",
                            "2021-03-09"
                        )
                    )
                ),
                TermEventsDto(
                    "2020-2021-2",
                    listOf(
                        EventDto(
                            "Interrupção de atividades letivas (Páscoa)",
                            "2021-03-29",
                            "2021-04-05"
                        ),
                        EventDto(
                            "Período de ausência de atividade letiva (férias)",
                            "2021-08-01",
                            "2021-08-31"
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
                            "Exames de época especial",
                            "2021-09-02",
                            "2021-09-18",
                            false
                        )
                    ),
                    listOf(
                        LecturesDto(
                            "Todas as turmas",
                            listOf(IdDto(1), IdDto(2), IdDto(3), IdDto(4), IdDto(5), IdDto(6)),
                            "2021-03-15",
                            "2021-06-26"
                        )
                    ),
                    listOf(
                        EventDto(
                            "Divulgação de horários",
                            "2021-02-22",
                            "2021-02-22"
                        ),
                        EventDto(
                            "Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)",
                            "2021-09-02",
                            "2021-09-02"
                        ),
                        EventDto(
                            "Encerramento das atividades letivas 2020/2021",
                            "2021-07-31",
                            "2021-07-31"
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

        assertEquals(academicCalendarRetrievedDTO, academicCalendarExpectedDto)
    }
}
