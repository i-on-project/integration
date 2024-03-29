package org.ionproject.integration.application.job.chunkbased

import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.application.job.ISELTimetableJob
import org.ionproject.integration.application.job.TIMETABLE_JOB_NAME
import org.ionproject.integration.infrastructure.CompositeException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File
import java.lang.reflect.UndeclaredThrowableException
import java.nio.file.Paths
import java.time.Instant

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetableJob::class,
        FormatVerifierStepBuilder::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
@SpringBootTest
class FormatVerifierStepTestSuccessful {

    @Autowired
    @Qualifier(value = TIMETABLE_JOB_NAME)
    private lateinit var job: Job

    @Autowired
    private lateinit var jobLauncher: JobLauncher

    @Autowired
    private lateinit var jobRepository: JobRepository

    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @BeforeEach
    private fun initializeJobLauncherTestUtils() {
        jobLauncherTestUtils = JobLauncherTestUtils()
        jobLauncherTestUtils.jobLauncher = jobLauncher
        jobLauncherTestUtils.jobRepository = jobRepository
        jobLauncherTestUtils.job = job
    }

    @Autowired
    private lateinit var state: ISELTimetableJob.State

    private val utils = SpringBatchTestUtils()
    private val json =
        "[{\"extraction_method\":\"lattice\",\"top\":70.61023,\"left\":56.7,\"width\":481.9666748046875,\"height\":38.750816345214844,\"right\":538.6667,\"bottom\":109.361046,\"data\":[[{\"top\":70.61023,\"left\":56.7,\"width\":240.90000915527344,\"height\":19.450233459472656,\"text\":\"Table header 1\"},{\"top\":70.61023,\"left\":297.6,\"width\":241.06668090820312,\"height\":19.450233459472656,\"text\":\"Table header 2\"}],[{\"top\":90.06046,\"left\":56.7,\"width\":240.90000915527344,\"height\":19.300582885742188,\"text\":\"1\"},{\"top\":90.06046,\"left\":297.6,\"width\":241.06668090820312,\"height\":19.300582885742188,\"text\":\"2\"}]]}]"
    private val text = listOf("Turma: LI11D Ano Letivo: 2019/20-Verão\nTable header 1 Table header 2\n1 2")

    @Test
    fun whenStepIsSuccessful_thenAssertFileDoesNotExistAndRawDataIsCompleteAndHashIsInContext() {
        // Arrange
        val src = File("src/test/resources/formatTest.pdf".replace("/", File.separator))
        val temp = File("src/test/resources/formatVerifierStepTestSuccess.pdf".replace("/", File.separator))
        src.copyTo(temp)

        // SHA256 digest from src file
        val expectedHash = byteArrayOf(
            -70, -110, -93, -28, -124, 69, 56, -46, -91, -76, 41, 111, -107, -35, -112,
            27, 124, -55, 81, 40, 84, 37, 44, 85, 41, 63, -116, -8, -19, 84, -15, -50
        )
        val jp = initJobParameters()
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put("file-path", temp.toPath())
        val ec = se.jobExecution.executionContext

        // Act
        val je = jobLauncherTestUtils.launchStep(
            "Verify Format",
            jp,
            ec
        )

        temp.delete()

        // Assert
        val hash = je.executionContext["file-hash"] as ByteArray
        assertEquals(text, state.rawTimetableData.textData)
        assertEquals(json, state.rawTimetableData.scheduleData)
        assertEquals(ExitStatus.COMPLETED, je.exitStatus)
        assertTrue(expectedHash.contentEquals(hash))
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetableJob::class,
        FormatVerifierStepBuilder::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
@SpringBootTest
internal class FormatVerifierStepTestUnexistingFile {

    @Autowired
    @Qualifier(value = TIMETABLE_JOB_NAME)
    private lateinit var job: Job

    @Autowired
    private lateinit var jobLauncher: JobLauncher

    @Autowired
    private lateinit var jobRepository: JobRepository

    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @BeforeEach
    private fun initializeJobLauncherTestUtils() {
        jobLauncherTestUtils = JobLauncherTestUtils()
        jobLauncherTestUtils.jobLauncher = jobLauncher
        jobLauncherTestUtils.jobRepository = jobRepository
        jobLauncherTestUtils.job = job
    }

    private val utils = SpringBatchTestUtils()

    @Test
    fun whenFileDoesntExist_thenAssertThrowsException() {
        // Arrange
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put("file-path", Paths.get("src/test/resources/UnexistingFile.pdf"))

        // Act
        val jp = initJobParameters()
        val je = jobLauncherTestUtils.launchStep(
            "Verify Format",
            jp,
            se.jobExecution.executionContext
        )
        val ex = je.allFailureExceptions[0] as UndeclaredThrowableException
        val cex = ex.undeclaredThrowable as CompositeException
        // Assert
        assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        assertionsExceptions(cex)
    }

    private fun assertionsExceptions(ex: CompositeException) {
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
        ISELTimetableJob::class,
        FormatVerifierStepBuilder::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
@SpringBootTest
internal class FormatVerifierStepTestInvalidFormat {

    @Autowired
    @Qualifier(value = TIMETABLE_JOB_NAME)
    private lateinit var job: Job

    @Autowired
    private lateinit var jobLauncher: JobLauncher

    @Autowired
    private lateinit var jobRepository: JobRepository

    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @BeforeEach
    private fun initializeJobLauncherTestUtils() {
        jobLauncherTestUtils = JobLauncherTestUtils()
        jobLauncherTestUtils.jobLauncher = jobLauncher
        jobLauncherTestUtils.jobRepository = jobRepository
        jobLauncherTestUtils.job = job
    }

    private val utils = SpringBatchTestUtils()

    @Test
    fun `when file has invalid format then assert an exception is thrown`() {
        // Arrange
        val src = File("src/test/resources/test.pdf".replace("/", File.separator))
        val temp = File("src/test/resources/formatVerifierStepTestTemp.pdf".replace("/", File.separator))
        try {

            src.copyTo(temp)

            val jp = initJobParameters()
            val se = utils.createStepExecution()
            se.jobExecution.executionContext.put("file-path", temp.toPath())

            // Act
            val je = jobLauncherTestUtils.launchStep(
                "Verify Format",
                jp,
                se.jobExecution.executionContext
            )
            // Assert
            assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
            val ex = je.allFailureExceptions[0] as UndeclaredThrowableException
            assertEquals("FormatCheckException", ex.undeclaredThrowable::class.java.simpleName)
            assertEquals("The timetable header changed its format", ex.undeclaredThrowable.message)
        } finally {
            assertFalse(temp.delete())
        }
    }

    @Test
    fun `when File has invalid format then assert mail is sent`() {
        // Arrange
        val src = File("src/test/resources/test.pdf".replace("/", File.separator))
        val temp = File("src/test/resources/formatVerifierStepTestTemp2.pdf".replace("/", File.separator))
        try {

            src.copyTo(temp)

            val jp = initJobParameters()
            val se = utils.createStepExecution()
            se.jobExecution.executionContext.put(
                "file-path",
                Paths.get("src/test/resources/formatVerifierStepTestTemp2.pdf")
            )

            // Act
            val je = jobLauncherTestUtils.launchStep(
                "Verify Format",
                jp,
                se.jobExecution.executionContext
            )

            // Assert
            assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        } finally {
            assertFalse(temp.delete())
        }
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetableJob::class,
        FormatVerifierStepBuilder::class,
        IOnIntegrationApplication::class
    ]
)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
internal class FormatVerifierStepTestEmptyPath {

    @Autowired
    @Qualifier(value = TIMETABLE_JOB_NAME)
    private lateinit var job: Job

    @Autowired
    private lateinit var jobLauncher: JobLauncher

    @Autowired
    private lateinit var jobRepository: JobRepository

    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @BeforeEach
    private fun initializeJobLauncherTestUtils() {
        jobLauncherTestUtils = JobLauncherTestUtils()
        jobLauncherTestUtils.jobLauncher = jobLauncher
        jobLauncherTestUtils.jobRepository = jobRepository
        jobLauncherTestUtils.job = job
    }

    private val utils = SpringBatchTestUtils()

    @Test
    fun whenPathIsEmpty_thenAssertThrowsException() {
        // Arrange
        val jp = initJobParameters()
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put("file-path", Paths.get(""))

        // Act
        val je = jobLauncherTestUtils.launchStep(
            "Verify Format",
            jp,
            se.jobExecution.executionContext
        )
        // Assert
        assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        val ex = je.allFailureExceptions[0] as UndeclaredThrowableException
        val cex = ex.undeclaredThrowable as CompositeException
        assertEquals("PdfExtractorException", cex.exceptions[0]::class.java.simpleName)
        assertEquals("Empty path", cex.exceptions[0].message)
    }
}

private fun initJobParameters(): JobParameters {
    return JobParametersBuilder()
        .addString("srcRemoteLocation", "https://www.isel.pt/media/uploads/LEIC_0310.pdf")
        .addString("alertRecipient", "client@domain.com")
        .addLong("timestamp", Instant.now().toEpochMilli())
        .toJobParameters()
}
