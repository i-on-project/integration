package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import com.icegreen.greenmail.util.DummySSLSocketFactory
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.GreenMailUtil
import com.icegreen.greenmail.util.ServerSetupTest
import java.io.File
import java.lang.reflect.UndeclaredThrowableException
import java.security.Security
import java.time.Instant
import javax.mail.internet.MimeMessage
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.step.tasklet.iseltimetable.exceptions.DownloadAndCompareTaskletException
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.ionproject.integration.utils.CompositeException
import org.junit.FixMethodOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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
        ISELTimetable::class,
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
    @Qualifier(value = "timetableJob")
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

    @Test
    fun whenTaskletIsUnsuccessful_ThenAssertPathIsInContextAndFileExists() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val pathKey = "file-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters("1")
        val file = (appProperties.tempFilesDir + "LEIC_0310.pdf").asFile
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
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val pathKey = "file-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters("2")
        val file = (appProperties.tempFilesDir + "LEIC_0310.pdf").asFile
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

    @Test
    @Sql("insert-timetable-pdf-hash-2.sql")
    fun whenTaskletIsSuccessful_ThenAssertMailWasSent() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val pathKey = "file-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters("3")
        val file = (appProperties.tempFilesDir + "LEIC_0310.pdf").asFile
        val expectedPath = file.toPath()
        try {

            val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)

            assertEquals(ExitStatus.STOPPED.exitCode, je.exitStatus.exitCode)
            assertEquals(expectedPath, je.executionContext[pathKey])
            assertFalse(file.exists())

            val messages: Array<MimeMessage> = testSmtp.receivedMessages
            assertEquals(0, messages.size)
        } finally {
            file.delete()
        }
    }

    private fun initJobParameters(jobId: String): JobParameters {
        return JobParametersBuilder()
            .addString("srcRemoteLocation", "https://www.isel.pt/media/uploads/LEIC_0310.pdf")
            .addString("alertRecipient", "client@domain.com")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .addString("jobId", jobId)
            .toJobParameters()
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetable::class,
        DownloadAndCompareTasklet::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
internal class DownloadAndCompareTaskletMissingPropertiesTest {

    @Autowired
    @Qualifier(value = "timetableJob")
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

    @Test
    fun whenUrlIsNotDefined_ThenReturnsIllegalArgumentExceptionAndPathIsNotIncludedInContext() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
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
        ISELTimetable::class,
        DownloadAndCompareTasklet::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
internal class DownloadAndCompareTaskletUrlNotPdfTest {

    @Autowired
    @Qualifier(value = "timetableJob")
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
        ISELTimetable::class,
        DownloadAndCompareTasklet::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
internal class DownloadAndCompareTaskletServerErrorTest {

    @Autowired
    @Qualifier(value = "timetableJob")
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

    @Test
    fun whenServerResponds5xx_ThenAssertExceptionIsServerErrorAndPathIsNotInContext() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val localFileDestination = "src/test/resources/SERVER_DOWN.pdf"
        val pathKey = "file-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters()
        val file = File(localFileDestination)
        try {
            val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)

            val actualPath = je.executionContext.get(pathKey)
            assertNull(actualPath)
            val ute = je.allFailureExceptions[0] as UndeclaredThrowableException
            val compEx = ute.undeclaredThrowable as CompositeException
            assertEquals("ServerErrorException", compEx.exceptions[0]::class.java.simpleName)
            assertEquals("Server responded with error code 500", compEx.exceptions[0].message)
            assertFalse(file.exists())
        } finally {
            file.deleteOnExit()
        }
    }

    @Disabled
    @Test
    fun whenServerResponds5xx_ThenAssertAlertWasSent() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val localFileDestination = "src/test/resources/SERVER_DOWN.pdf"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters()
        val file = File(localFileDestination)
        try {
            jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)

            val messages: Array<MimeMessage> = testSmtp.receivedMessages
            assertEquals(1, messages.size)
            assertEquals("i-on integration Alert - Job FAILED", messages[0].subject)
            assertTrue(
                GreenMailUtil.getBody(messages[0])
                    .contains("TestJob FAILED for file: 500 with message Server responded with error code 500")
            )
        } finally {
            file.deleteOnExit()
        }
    }

    private fun initJobParameters(): JobParameters {
        return JobParametersBuilder()
            .addString("srcRemoteLocation", "http://httpstat.us/500")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .addString("alertRecipient", "client@domain.com")
            .toJobParameters()
    }
}
