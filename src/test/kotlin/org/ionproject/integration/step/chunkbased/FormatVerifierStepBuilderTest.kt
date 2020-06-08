package org.ionproject.integration.step.chunkbased

import java.io.File
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.ExitStatus
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

    @Autowired
    private lateinit var state: ISELTimetable.State

    private val utils = SpringBatchTestUtils()
    private val json = "[{\"extraction_method\":\"lattice\",\"top\":70.61023,\"left\":56.7,\"width\":481.9666748046875,\"height\":38.750816345214844,\"right\":538.6667,\"bottom\":109.361046,\"data\":[[{\"top\":70.61023,\"left\":56.7,\"width\":241.00001525878906,\"height\":19.450233459472656,\"text\":\"Table header 1\"},{\"top\":70.61023,\"left\":297.7,\"width\":240.9666748046875,\"height\":19.450233459472656,\"text\":\"Table header 2\"}],[{\"top\":90.06046,\"left\":56.7,\"width\":241.00001525878906,\"height\":19.300582885742188,\"text\":\"1\"},{\"top\":90.06046,\"left\":297.7,\"width\":240.9666748046875,\"height\":19.300582885742188,\"text\":\"2\"}]]}]"
    private val text = listOf("Turma: LI11D Ano Letivo: 2019/20-Verão\nTable header 1 Table header 2\n1 2")
    @Test
    fun whenSuccessAlertIsSent_thenAssertX() {
        // Arrange
        val src = File("src/test/resources/formatTest.pdf")
        val temp = props.localFileDestination.toFile()
        src.copyTo(temp)
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put(props.pdfKey, props.localFileDestination)

        // Act
        val je = jobLauncherTestUtils.launchStep("Verify Format", se.jobExecution.executionContext)
        // Assert
        // how to test alert sent??
        assertFalse(temp.exists())
        assertEquals(text, state.rawData.textData)
        assertEquals(json, state.rawData.jsonData)
        assertEquals(ExitStatus.COMPLETED, je.exitStatus)
    }
}
