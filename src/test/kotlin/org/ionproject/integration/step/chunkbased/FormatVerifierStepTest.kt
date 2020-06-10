package org.ionproject.integration.step.chunkbased

import java.io.File
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.ionproject.integration.utils.CompositeException
import org.junit.jupiter.api.Assertions.assertEquals
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
        "isel-timetable.localFileDestination=src/test/resources/formatVerifierStepTest.pdf"
    ]
)
@SpringBatchTest
internal class FormatVerifierStepTestSuccessful {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var props: ISELTimetableProperties

    @Autowired
    private lateinit var state: ISELTimetable.State

    private val utils = SpringBatchTestUtils()
    private val json =
        "[{\"extraction_method\":\"lattice\",\"top\":70.61023,\"left\":56.7,\"width\":481.9666748046875,\"height\":38.750816345214844,\"right\":538.6667,\"bottom\":109.361046,\"data\":[[{\"top\":70.61023,\"left\":56.7,\"width\":240.90000915527344,\"height\":19.450233459472656,\"text\":\"Table header 1\"},{\"top\":70.61023,\"left\":297.6,\"width\":241.06668090820312,\"height\":19.450233459472656,\"text\":\"Table header 2\"}],[{\"top\":90.06046,\"left\":56.7,\"width\":240.90000915527344,\"height\":19.300582885742188,\"text\":\"1\"},{\"top\":90.06046,\"left\":297.6,\"width\":241.06668090820312,\"height\":19.300582885742188,\"text\":\"2\"}]]}]"
    private val text = listOf("Turma: LI11D Ano Letivo: 2019/20-Verão\nTable header 1 Table header 2\n1 2")

    @Test
    fun whenStepIsSuccessful_thenAssertFileDoesNotExistAndRawDataIsComplete() {
        // Arrange
        val src = File("src/test/resources/formatTest.pdf")
        val temp = props.localFileDestination.toFile()
        src.copyTo(temp)
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put(props.pdfKey, props.localFileDestination)

        // Act
        val je = jobLauncherTestUtils.launchStep("Verify Format", se.jobExecution.executionContext)
        // Assert
        assertEquals(text, state.rawData.textData)
        assertEquals(json, state.rawData.jsonData)
        assertEquals(ExitStatus.COMPLETED, je.exitStatus)
    }
}

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
        "isel-timetable.localFileDestination=src/test/resources/formatVerifierStepTest.pdf"
    ]
)
@SpringBatchTest
internal class FormatVerifierStepTestUnexistingFile {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var props: ISELTimetableProperties

    private val utils = SpringBatchTestUtils()

    @Test
    fun whenFileDoesntExist_thenAssertThrowsException() {
        // Arrange
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put(props.pdfKey, props.localFileDestination)

        // Act
        val je = jobLauncherTestUtils.launchStep(
            "Verify Format",
            se.jobExecution.executionContext
        )
        val ex = je.allFailureExceptions[0] as CompositeException
        // Assert
        assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        assertionsNonExistantFile(ex)
    }

    private fun assertionsNonExistantFile(ex: CompositeException) {
        assertEquals("IOException", ex.exceptions[0]::class.java.simpleName)
        assertEquals("PdfExtractorException", ex.exceptions[1]::class.java.simpleName)
        assertEquals("Itext cannot process file", ex.exceptions[1].message)
        assertEquals("PdfExtractorException", ex.exceptions[2]::class.java.simpleName)
        assertEquals("File doesn't exist", ex.exceptions[2].message)
    }
}

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
        "isel-timetable.localFileDestination=src/test/resources/formatVerifierStepTest.pdf"
    ]
)
@SpringBatchTest
internal class FormatVerifierStepTestInvalidFormat {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var props: ISELTimetableProperties

    private val utils = SpringBatchTestUtils()
    @Test
    fun whenFileHasInvalidFormat_thenAssertThrowsException() {
        // Arrange
        val src = File("src/test/resources/test.pdf")
        val temp = props.localFileDestination.toFile()
        src.copyTo(temp)
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put(props.pdfKey, props.localFileDestination)

        // Act
        val je = jobLauncherTestUtils.launchStep(
            "Verify Format",
            se.jobExecution.executionContext
        )
        // Assert
        assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        val ex = je.allFailureExceptions[0]
        assertEquals("FormatCheckException", ex::class.java.simpleName)
        assertEquals("The timetable header changed its format", ex.message)
    }
}
