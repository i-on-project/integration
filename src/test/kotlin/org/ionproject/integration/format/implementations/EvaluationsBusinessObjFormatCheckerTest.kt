package org.ionproject.integration.format.implementations

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dispatcher.IDispatcher
import org.ionproject.integration.application.job.ISELEvaluationsJob
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.domain.common.ProgrammeResources
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.evaluations.Evaluations
import org.ionproject.integration.domain.evaluations.Exam
import org.ionproject.integration.domain.evaluations.ExamCategory
import org.ionproject.integration.infrastructure.http.IFileDownloader
import org.ionproject.integration.infrastructure.repository.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.IProgrammeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import java.io.File
import java.net.URI
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

        val evaluationsRetrieved = Evaluations.from(evaluationsData, programme)

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
                        "SO-leic",
                        ZonedDateTime.of(2021, 7, 9, 10, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 9, 13, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SO-leic",
                        ZonedDateTime.of(2021, 7, 26, 19, 0, 0, 0, ZoneId.systemDefault()),
                        ZonedDateTime.of(2021, 7, 26, 22, 0, 0, 0, ZoneId.systemDefault()),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SO-leic",
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

        assertEquals(evaluationsExpected, evaluationsRetrieved)
    }
}
