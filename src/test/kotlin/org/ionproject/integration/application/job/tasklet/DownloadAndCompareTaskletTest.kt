package org.ionproject.integration.application.job.tasklet

import com.icegreen.greenmail.util.DummySSLSocketFactory
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetupTest
import java.io.File
import java.lang.reflect.UndeclaredThrowableException
import java.security.Security
import java.time.Instant
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.application.JobEngine.Companion.JOB_HASH_PARAMETER
import org.ionproject.integration.application.JobEngine.Companion.TIMESTAMP_PARAMETER
import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.exception.DownloadAndCompareTaskletException
import org.ionproject.integration.application.job.ISELTimetableJob
import org.ionproject.integration.application.job.TIMETABLE_JOB_NAME
import org.ionproject.integration.application.job.chunkbased.SpringBatchTestUtils
import org.junit.FixMethodOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runners.MethodSorters
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
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetableJob::class,
        DownloadAndCompareTasklet::class,
        IOnIntegrationApplication::class
    ]
)

@TestPropertySource("classpath:application.properties")
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
internal class DownloadAndCompareTaskletDownloadSuccessfulButHashTheSameAsRecorded {

    @Autowired
    private lateinit var appProperties: AppProperties

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

    val utils = SpringBatchTestUtils()

    @Test
    fun whenTaskletIsUnsuccessful_ThenAssertPathIsInContextAndFileExists() {
        val pathKey = "file-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters("1")
        val file = File(appProperties.tempFilesDir.path + File.separator + "LEIC_0310.pdf")
        val expectedPath = file.toPath()
        try {

            val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)

            assertEquals(ExitStatus.COMPLETED.exitCode, je.exitStatus.exitCode)
            assertEquals(expectedPath, je.executionContext[pathKey])
            assertTrue(file.exists())
        } finally {
            file.delete()
        }
    }

    @Test
    @Sql("insert-timetable-pdf-hash.sql")
    fun whenHashIsSameAsRecorded_ThenExitStatusIsStopped() {
        val pathKey = "file-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters("2")
        val file = File(appProperties.tempFilesDir.path + File.separator + "LEIC_0310.pdf")
        val expectedPath = file.toPath()
        try {
            val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)

            assertEquals(ExitStatus.STOPPED.exitCode, je.exitStatus.exitCode)
            assertEquals(expectedPath, je.executionContext[pathKey])
            assertFalse(file.exists())
        } finally {
            file.delete()
        }
    }

    private fun initJobParameters(jobId: String): JobParameters {
        return JobParametersBuilder()
            .addString("srcRemoteLocation", "https://www.isel.pt/media/uploads/LEIC_0310.pdf")
            .addLong(TIMESTAMP_PARAMETER, Instant.now().toEpochMilli())
            .addString(JOB_HASH_PARAMETER, jobId)
            .toJobParameters()
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetableJob::class,
        DownloadAndCompareTasklet::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
internal class DownloadAndCompareTaskletMissingPropertiesTest {

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

    val utils = SpringBatchTestUtils()

    @Test
    fun whenUrlIsNotDefined_ThenReturnsIllegalArgumentExceptionAndPathIsNotIncludedInContext() {
        val localFileDestination = "src/test/resources/TIMETABLE.pdf"
        val pathKey = "file-path"
        val file = File(localFileDestination)
        val ec = utils.createExecutionContext()
        val jp = initJobParameters()
        try {
            val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)
            assertFalse(file.exists())
            val actualPath = je.executionContext[pathKey]
            assertNull(actualPath)
        } finally {
            file.deleteOnExit()
        }
    }

    private fun initJobParameters(): JobParameters {
        return JobParametersBuilder()
            .addString("srcRemoteLocation", "")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .toJobParameters()
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetableJob::class,
        DownloadAndCompareTasklet::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
internal class DownloadAndCompareTaskletUrlNotPdfTest {

    @Autowired
    @Qualifier(value = TIMETABLE_JOB_NAME)
    private lateinit var job: Job

    @Autowired
    private lateinit var appProperties: AppProperties

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

    val utils = SpringBatchTestUtils()

    private lateinit var testSmtp: GreenMail

    @BeforeEach
    fun testSmtpInit() {
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory::class.java.name)
        testSmtp = GreenMail(ServerSetupTest.SMTP)
        testSmtp.start()
    }

    @AfterEach
    fun stopMailServer() {
        testSmtp.stop()
    }

    @Test
    fun whenUrlIsNotPdf_ThenAssertExceptionIsInvalidFormatAndPathIsNotIncludedInContext() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val localFileDestination = appProperties.tempFilesDir.path
        val pathKey = "file-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters()
        val file = File(localFileDestination)
        val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)
        val actualPath = je.executionContext.get(pathKey)
        val ex = je.allFailureExceptions[0] as UndeclaredThrowableException
        assertEquals("DownloadAndCompareTaskletException", ex.undeclaredThrowable::class.java.simpleName)
        val dCTEx = ex.undeclaredThrowable as DownloadAndCompareTaskletException
        assertEquals("Specified path $localFileDestination is a directory", dCTEx.message)
        assertNull(actualPath)
        assertTrue(file.isDirectory)
    }

    private fun initJobParameters(): JobParameters {
        return JobParametersBuilder()
            .addString("srcRemoteLocation", "https://kotlinlang.org/")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .toJobParameters()
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetableJob::class,
        DownloadAndCompareTasklet::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
internal class DownloadAndCompareTaskletServerErrorTest {

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

    val utils = SpringBatchTestUtils()

    private lateinit var testSmtp: GreenMail

    @BeforeEach
    fun testSmtpInit() {
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory::class.java.name)
        testSmtp = GreenMail(ServerSetupTest.SMTP)
        testSmtp.start()
    }

    @AfterEach
    fun stopMailServer() {
        testSmtp.stop()
    }

    private fun initJobParameters(): JobParameters {
        return JobParametersBuilder()
            .addString("srcRemoteLocation", "http://httpstat.us/500")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .addString("alertRecipient", "client@domain.com")
            .toJobParameters()
    }
}
