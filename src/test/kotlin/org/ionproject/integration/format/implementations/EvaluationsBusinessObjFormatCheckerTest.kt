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
import java.time.LocalDateTime
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

        val academicCalendarRetrieved = Evaluations.from(evaluationsData, programme)

        val academicCalendarExpected =
            Evaluations(
                academicCalendarRetrieved.creationDateTime, // 2020-2021 Evaluations PDF doesn't have a creation date in its properties, so it gets the retrieval date time.
                academicCalendarRetrieved.retrievalDateTime,
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
                        LocalDateTime.of(2021, 7, 7, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 7, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AApl",
                        LocalDateTime.of(2021, 7, 24, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 24, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AApl",
                        LocalDateTime.of(2021, 9, 9, 14, 0, 0),
                        LocalDateTime.of(2021, 9, 9, 17, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "AC",
                        LocalDateTime.of(2021, 7, 1, 14, 0, 0),
                        LocalDateTime.of(2021, 7, 1, 17, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AC",
                        LocalDateTime.of(2021, 7, 19, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 19, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AC",
                        LocalDateTime.of(2021, 9, 7, 10, 0, 0),
                        LocalDateTime.of(2021, 9, 7, 13, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "AED",
                        LocalDateTime.of(2021, 6, 28, 19, 0, 0),
                        LocalDateTime.of(2021, 6, 28, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AED",
                        LocalDateTime.of(2021, 7, 20, 14, 0, 0),
                        LocalDateTime.of(2021, 7, 20, 17, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AED",
                        LocalDateTime.of(2021, 9, 16, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 16, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "ALGA",
                        LocalDateTime.of(2021, 7, 8, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 8, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "ALGA",
                        LocalDateTime.of(2021, 7, 23, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 23, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "ALGA",
                        LocalDateTime.of(2021, 9, 8, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 8, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "AVE",
                        LocalDateTime.of(2021, 7, 5, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 5, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "AVE",
                        LocalDateTime.of(2021, 7, 22, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 22, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "AVE",
                        LocalDateTime.of(2021, 9, 15, 14, 0, 0),
                        LocalDateTime.of(2021, 9, 15, 17, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CDI",
                        LocalDateTime.of(2021, 7, 16, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 16, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "CDI",
                        LocalDateTime.of(2021, 7, 30, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 30, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "CDI",
                        LocalDateTime.of(2021, 9, 17, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 17, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CN",
                        LocalDateTime.of(2021, 7, 15, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 15, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "CN",
                        LocalDateTime.of(2021, 7, 29, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 29, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "CN",
                        LocalDateTime.of(2021, 9, 10, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 10, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "COM",
                        LocalDateTime.of(2021, 7, 12, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 12, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "COM",
                        LocalDateTime.of(2021, 7, 26, 14, 0, 0),
                        LocalDateTime.of(2021, 7, 26, 17, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "COM",
                        LocalDateTime.of(2021, 9, 13, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 13, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CQF",
                        LocalDateTime.of(2021, 9, 8, 10, 0, 0),
                        LocalDateTime.of(2021, 9, 8, 13, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "CSM",
                        LocalDateTime.of(2021, 7, 12, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 12, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "CSM",
                        LocalDateTime.of(2021, 7, 31, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 31, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "CSM",
                        LocalDateTime.of(2021, 9, 8, 10, 0, 0),
                        LocalDateTime.of(2021, 9, 8, 13, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "DAW",
                        LocalDateTime.of(2021, 6, 28, 19, 0, 0),
                        LocalDateTime.of(2021, 6, 28, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "DAW",
                        LocalDateTime.of(2021, 7, 21, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 21, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "DAW",
                        LocalDateTime.of(2021, 9, 15, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 15, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "EGP",
                        LocalDateTime.of(2021, 7, 1, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 1, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "EGP",
                        LocalDateTime.of(2021, 7, 20, 14, 0, 0),
                        LocalDateTime.of(2021, 7, 20, 17, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "EGP",
                        LocalDateTime.of(2021, 9, 9, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 9, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "Eltr",
                        LocalDateTime.of(2021, 7, 13, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 13, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "Eltr",
                        LocalDateTime.of(2021, 7, 27, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 27, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "Eltr",
                        LocalDateTime.of(2021, 9, 15, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 15, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "Emp",
                        LocalDateTime.of(2021, 9, 16, 18, 30),
                        LocalDateTime.of(2021, 9, 16, 21, 30),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "GAP",
                        LocalDateTime.of(2021, 6, 30, 10, 0, 0),
                        LocalDateTime.of(2021, 6, 30, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "GAP",
                        LocalDateTime.of(2021, 7, 19, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 19, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "GAP",
                        LocalDateTime.of(2021, 9, 3, 14, 0, 0),
                        LocalDateTime.of(2021, 9, 3, 17, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "GQS",
                        LocalDateTime.of(2021, 9, 7, 18, 30),
                        LocalDateTime.of(2021, 9, 7, 21, 30),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "IASA",
                        LocalDateTime.of(2021, 7, 2, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 2, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "IASA",
                        LocalDateTime.of(2021, 7, 20, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 20, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "IASA",
                        LocalDateTime.of(2021, 9, 2, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 2, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "IEB",
                        LocalDateTime.of(2021, 9, 6, 10, 0, 0),
                        LocalDateTime.of(2021, 9, 6, 13, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "LC",
                        LocalDateTime.of(2021, 9, 14, 10, 0, 0),
                        LocalDateTime.of(2021, 9, 14, 13, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "LSD",
                        LocalDateTime.of(2021, 7, 6, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 6, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "LSD",
                        LocalDateTime.of(2021, 7, 21, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 21, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "LSD",
                        LocalDateTime.of(2021, 9, 6, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 6, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PC",
                        LocalDateTime.of(2021, 7, 13, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 13, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PC",
                        LocalDateTime.of(2021, 7, 27, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 27, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PC",
                        LocalDateTime.of(2021, 9, 17, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 17, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PDM",
                        LocalDateTime.of(2021, 9, 14, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 14, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PE",
                        LocalDateTime.of(2021, 7, 5, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 5, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PE",
                        LocalDateTime.of(2021, 7, 22, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 22, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PE",
                        LocalDateTime.of(2021, 9, 9, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 9, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PG",
                        LocalDateTime.of(2021, 6, 29, 19, 0, 0),
                        LocalDateTime.of(2021, 6, 29, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PG",
                        LocalDateTime.of(2021, 7, 23, 14, 0, 0),
                        LocalDateTime.of(2021, 7, 23, 17, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PG",
                        LocalDateTime.of(2021, 9, 8, 14, 0, 0),
                        LocalDateTime.of(2021, 9, 8, 17, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PI",
                        LocalDateTime.of(2021, 7, 2, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 2, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PI",
                        LocalDateTime.of(2021, 7, 21, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 21, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PI",
                        LocalDateTime.of(2021, 9, 10, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 10, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "POO",
                        LocalDateTime.of(2021, 9, 14, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 14, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "PSC",
                        LocalDateTime.of(2021, 7, 6, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 6, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "PSC",
                        LocalDateTime.of(2021, 7, 21, 14, 0, 0),
                        LocalDateTime.of(2021, 7, 21, 17, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "PSC",
                        LocalDateTime.of(2021, 9, 10, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 10, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "RCp",
                        LocalDateTime.of(2021, 7, 14, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 14, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "RCp",
                        LocalDateTime.of(2021, 7, 28, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 28, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "RCp",
                        LocalDateTime.of(2021, 9, 13, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 13, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "RI",
                        LocalDateTime.of(2021, 9, 7, 14, 0, 0),
                        LocalDateTime.of(2021, 9, 7, 17, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SE1",
                        LocalDateTime.of(2021, 9, 6, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 6, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SE2",
                        LocalDateTime.of(2021, 7, 9, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 9, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SE2",
                        LocalDateTime.of(2021, 7, 26, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 26, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SE2",
                        LocalDateTime.of(2021, 9, 6, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 6, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SG",
                        LocalDateTime.of(2021, 7, 1, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 1, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SG",
                        LocalDateTime.of(2021, 7, 20, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 20, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SG",
                        LocalDateTime.of(2021, 9, 8, 14, 0, 0),
                        LocalDateTime.of(2021, 9, 8, 17, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SegInf",
                        LocalDateTime.of(2021, 9, 17, 14, 0, 0),
                        LocalDateTime.of(2021, 9, 17, 17, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SI1",
                        LocalDateTime.of(2021, 6, 30, 19, 0, 0),
                        LocalDateTime.of(2021, 6, 30, 22, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SI1",
                        LocalDateTime.of(2021, 7, 19, 14, 0, 0),
                        LocalDateTime.of(2021, 7, 19, 17, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SI1",
                        LocalDateTime.of(2021, 9, 3, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 3, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SI2",
                        LocalDateTime.of(2021, 7, 8, 14, 0, 0),
                        LocalDateTime.of(2021, 7, 8, 17, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SI2",
                        LocalDateTime.of(2021, 7, 23, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 23, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SI2",
                        LocalDateTime.of(2021, 9, 13, 10, 0, 0),
                        LocalDateTime.of(2021, 9, 13, 13, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SM",
                        LocalDateTime.of(2021, 9, 15, 10, 0, 0),
                        LocalDateTime.of(2021, 9, 15, 13, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "SO-leic",
                        LocalDateTime.of(2021, 7, 9, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 9, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "SO-leic",
                        LocalDateTime.of(2021, 7, 26, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 26, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "SO-leic",
                        LocalDateTime.of(2021, 9, 7, 19, 0, 0),
                        LocalDateTime.of(2021, 9, 7, 22, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "TAR",
                        LocalDateTime.of(2021, 7, 16, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 16, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "TAR",
                        LocalDateTime.of(2021, 7, 30, 19, 0, 0),
                        LocalDateTime.of(2021, 7, 30, 22, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "TAR",
                        LocalDateTime.of(2021, 9, 7, 10, 0, 0),
                        LocalDateTime.of(2021, 9, 7, 13, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "TJ",
                        LocalDateTime.of(2021, 7, 12, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 12, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "TJ",
                        LocalDateTime.of(2021, 7, 28, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 28, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "TJ",
                        LocalDateTime.of(2021, 9, 17, 14, 0, 0),
                        LocalDateTime.of(2021, 9, 17, 17, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    ),
                    Exam(
                        "TMD",
                        LocalDateTime.of(2021, 7, 10, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 10, 13, 0, 0),
                        ExamCategory.EXAM_NORMAL,
                        ""
                    ),
                    Exam(
                        "TMD",
                        LocalDateTime.of(2021, 7, 29, 10, 0, 0),
                        LocalDateTime.of(2021, 7, 29, 13, 0, 0),
                        ExamCategory.EXAM_ALTERN,
                        ""
                    ),
                    Exam(
                        "TMD",
                        LocalDateTime.of(2021, 9, 13, 14, 0, 0),
                        LocalDateTime.of(2021, 9, 13, 17, 0, 0),
                        ExamCategory.EXAM_SPECIAL,
                        ""
                    )
                )
            )

        assertEquals(academicCalendarExpected, academicCalendarRetrieved)
    }
}
