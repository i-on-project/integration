package org.ionproject.integration.format.implementations

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dispatcher.IDispatcher
import org.ionproject.integration.application.job.ISELAcademicCalendarJob
import org.ionproject.integration.domain.common.Language
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.infrastructure.http.IFileDownloader
import org.ionproject.integration.model.external.calendar.AcademicCalendar
import org.ionproject.integration.model.external.calendar.Detail
import org.ionproject.integration.model.external.calendar.Evaluation
import org.ionproject.integration.model.external.calendar.Event
import org.ionproject.integration.model.external.calendar.Term
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import java.io.File
import java.time.LocalDate
import java.time.Month
import javax.sql.DataSource

class AcademicCalendarBusinessObjFormatCheckerTests {
    private val mockJobBuilderFactory = mock<JobBuilderFactory> {}

    private val mockStepBuilderFactory = mock<StepBuilderFactory> {}

    private val mockAppProperties = mock<AppProperties> {}

    private val mockDownloader = mock<IFileDownloader> {}

    private val mockDataSource = mock<DataSource> {}

    private val mockDispatcher = mock<IDispatcher> {}

    @Test
    fun `when given a academic calendar if business object extraction is as expected then success`() {
        val resourceFile = File("src/test/resources/calendarTest.pdf")

        val job = ISELAcademicCalendarJob(
            mockJobBuilderFactory,
            mockStepBuilderFactory,
            mockAppProperties,
            mockDownloader,
            mockDispatcher,
            mockDataSource
        )
        val calendarData = job.extractCalendarPDF(resourceFile.toPath().toString())

        val academicCalendar = AcademicCalendar.from(calendarData)
        val academicCalendarBO = AcademicCalendar(
            "2020-07-06T16:00:21Z",
            academicCalendar.retrievalDateTime,
            School("Instituto Superior de Engenharia de Lisboa", "ISEL"),
            Language.PT,
            listOf(
                Term(
                    "2020-2021-1",
                    listOf(
                        Event(
                            "Interrupção de atividades letivas (Natal)",
                            LocalDate.of(2020, Month.DECEMBER, 21),
                            LocalDate.of(2021, Month.JANUARY, 3)
                        ),
                        Event(
                            "Interrupção de atividades letivas (Carnaval)",
                            LocalDate.of(2021, Month.FEBRUARY, 15),
                            LocalDate.of(2021, Month.FEBRUARY, 16)
                        )
                    ),
                    listOf(
                        Evaluation(
                            "Período de exames (época normal)",
                            LocalDate.of(2021, Month.JANUARY, 25),
                            LocalDate.of(2021, Month.FEBRUARY, 13),
                            false
                        ),
                        Evaluation(
                            "Período de exames (época de recurso)",
                            LocalDate.of(2021, Month.FEBRUARY, 17),
                            LocalDate.of(2021, Month.MARCH, 2),
                            false
                        )
                    ),
                    listOf(
                        Detail(
                            "Aulas",
                            listOf<Int>(1, 2, 3, 4, 5, 6),
                            LocalDate.of(2020, Month.OCTOBER, 6),
                            LocalDate.of(2021, Month.JANUARY, 23)
                        )
                    ),
                    listOf(
                        Event(
                            "Divulgação de horários",
                            LocalDate.of(2020, Month.SEPTEMBER, 9),
                            LocalDate.of(2020, Month.SEPTEMBER, 9)
                        ),
                        Event(
                            "Data limite para lançamento de classificações no Portal Académico (frequência, exames de época normal e de época de recurso)",
                            LocalDate.of(2021, Month.MARCH, 9),
                            LocalDate.of(2021, Month.MARCH, 9)
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
                        ),
                        Event(
                            "Período de ausência de atividade letiva (férias)",
                            LocalDate.of(2021, Month.AUGUST, 1),
                            LocalDate.of(2021, Month.AUGUST, 31)
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
                        ),
                        Evaluation(
                            "Exames de época especial",
                            LocalDate.of(2021, Month.SEPTEMBER, 2),
                            LocalDate.of(2021, Month.SEPTEMBER, 18),
                            false
                        )
                    ),
                    listOf(
                        Detail(
                            "Aulas",
                            listOf<Int>(1, 2, 3, 4, 5, 6),
                            LocalDate.of(2021, Month.MARCH, 15),
                            LocalDate.of(2021, Month.JUNE, 26)
                        )
                    ),
                    listOf(
                        Event(
                            "Divulgação de horários",
                            LocalDate.of(2021, Month.FEBRUARY, 22),
                            LocalDate.of(2021, Month.FEBRUARY, 22)
                        ),
                        Event(
                            "Encerramento das atividades letivas 2020/2021",
                            LocalDate.of(2021, Month.JULY, 31),
                            LocalDate.of(2021, Month.JULY, 31)
                        ),
                        Event(
                            "Data limite para lançamento de classificações no Portal Académico (época especial)",
                            LocalDate.of(2021, Month.SEPTEMBER, 30),
                            LocalDate.of(2021, Month.SEPTEMBER, 30)
                        ),
                        Event(
                            "Data limite para entrega de trabalhos finais de licenciatura*",
                            LocalDate.of(2021, Month.SEPTEMBER, 30),
                            LocalDate.of(2021, Month.SEPTEMBER, 30)
                        ),
                        Event(
                            "Data limite para entrega de trabalhos finais de mestrado",
                            LocalDate.of(2021, Month.SEPTEMBER, 30),
                            LocalDate.of(2021, Month.SEPTEMBER, 30)
                        ),
                        Event(
                            "Fim do ano letivo 2020/2021",
                            LocalDate.of(2021, Month.SEPTEMBER, 30),
                            LocalDate.of(2021, Month.SEPTEMBER, 30)
                        )
                    )
                )
            )
        )

        assertEquals(academicCalendarBO, academicCalendar)
    }
}
