package org.ionproject.integration.format.implementations

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dispatcher.IDispatcher
import org.ionproject.integration.application.job.ISELEvaluationsJob
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.evaluations.Evaluations
import org.ionproject.integration.infrastructure.http.IFileDownloader
import org.ionproject.integration.infrastructure.repository.IInstitutionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import java.io.File
import java.net.URI
import javax.sql.DataSource

class EvaluationsBusinessObjFormatCheckerTest {

    private val mockJobBuilderFactory = mock<JobBuilderFactory> {}

    private val mockStepBuilderFactory = mock<StepBuilderFactory> {}

    private val mockAppProperties = mock<AppProperties> {}

    private val mockDownloader = mock<IFileDownloader> {}

    private val mockDispatcher = mock<IDispatcher> {}

    private val mockInstitutionRepository = mock<IInstitutionRepository> {}

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
            mockDataSource
        )
        val evaluationsData = job.extractEvaluationsPDF(resourceFile.toPath().toString())
        val institution = InstitutionModel(
            "Instituto Superior de Engenharia de Lisboa",
            "ISEL",
            "pt.ipl.isel",
            URI("")
        )
        val academicCalendarRetrieved = Evaluations.from(evaluationsData, institution)

        val academicCalendarExpected =
            Evaluations(
                academicCalendarRetrieved.creationDateTime, // 2020-2021 Evaluations PDF doesn't have a creation date in it's properties, so it gets the retrieval date time.
                academicCalendarRetrieved.retrievalDateTime,
                School(
                    "Instituto Superior de Engenharia de Lisboa",
                    "ISEL"
                ),
                "2020-2021-2",
                emptyList()
            )

        assertEquals(academicCalendarExpected, academicCalendarRetrieved)
    }
}
