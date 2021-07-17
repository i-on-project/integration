package org.ionproject.integration.domain.evaluations

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dispatcher.IDispatcher
import org.ionproject.integration.application.job.ISELEvaluationsJob
import org.ionproject.integration.domain.common.CalendarTerm
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.domain.common.ProgrammeResources
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.common.Term
import org.ionproject.integration.infrastructure.http.IFileDownloader
import org.ionproject.integration.infrastructure.repository.model.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.model.IProgrammeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import java.io.File
import java.net.URI
import java.time.Year
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.sql.DataSource

class EvaluationsBusinessObjFormatCheckerTest {

    private val mockJobBuilderFactory = mock<JobBuilderFactory> {}

    private val mockStepBuilderFactory = mock<StepBuilderFactory> {}

    private val mockAppProperties = mock<AppProperties> {}

    private val mockDownloader = mock<IFileDownloader> {}

    private val mockDispatcher = mock<IDispatcher> {}

    private val mockInstitutionRepository = mock<IInstitutionRepository> {}

    private val mockProgrammeRepository = mock<IProgrammeRepository> {}

    private val mockDataSource = mock<DataSource> {}

    private val EUROPE_LISBON_TIMEZONE = "Europe/Lisbon"

    @Test
    fun `when given an evaluations pdf if business object extraction is as expected then success`() {
        val resourceFile = File("src/test/resources/evaluationsTest.pdf")

        val job = ISELEvaluationsJob(
            mockJobBuilderFactory,
            mockStepBuilderFactory,
            mockAppProperties,
            mockDownloader,
            mockDispatcher,
            mockInstitutionRepository,
            mockProgrammeRepository,
            mockDataSource
        )
        val evaluationsData = job.extractEvaluationsPDF(resourceFile.toPath().toString())

        val institution = InstitutionModel(
            "Instituto Superior de Engenharia de Lisboa",
            "ISEL",
            "pt.ipl.isel",
            "Europe/Lisbon",
            URI("")
        )

        val programme = ProgrammeModel(
            institution,
            "Licenciatura em Engenharia Informática e de Computadores",
            "LEIC",
            ProgrammeResources(
                URI(""),
                URI("")
            )
        )

        val evaluationsRetrieved = Evaluations.from(evaluationsData, programme, EUROPE_LISBON_TIMEZONE)

        val evaluationsExpected =
            Evaluations(
                evaluationsRetrieved.creationDateTime, // 2020-2021 Evaluations PDF doesn't have a creation date in its properties, so it gets the retrieval date time.
                evaluationsRetrieved.retrievalDateTime,
                School(
                    "Instituto Superior de Engenharia de Lisboa",
                    "ISEL"
                ),
                Programme(
                    "Licenciatura em Engenharia Informática e de Computadores",
                    "LEIC"
                ),
                CalendarTerm(
                    Year.of(2020),
                    Year.of(2021),
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

        assertEquals(evaluationsExpected, evaluationsRetrieved)
    }
}
