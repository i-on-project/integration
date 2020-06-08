package org.ionproject.integration.step.chunkbased

import java.io.File
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetable::class,
        FormatVerifierStepBuilder::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource(
    properties = [
        "isel-timetable.pdfKey=pdf-key",
        "isel-timetable.localFileDestination=src/test/resources/formatTestTemp.pdf"
    ]
)
@SpringBatchTest
internal class FormatVerifierStepBuilderTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var props: ISELTimetableProperties

    private val utils = SpringBatchTestUtils()

    @Test
    fun whenSuccessAlertIsSent_thenAssertX() {
        val src = File("src/test/resources/formatTest.pdf")
        val temp = props.localFileDestination.toFile()
        src.copyTo(temp)
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put(props.pdfKey, props.localFileDestination)

        jobLauncherTestUtils.launchStep("Verify Format", se.jobExecution.executionContext)
        // how to test alert sent??
        assertFalse(temp.exists())
    }
}
