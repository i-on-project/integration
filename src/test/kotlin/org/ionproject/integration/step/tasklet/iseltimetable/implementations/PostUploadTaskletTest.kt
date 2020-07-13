package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import com.icegreen.greenmail.util.DummySSLSocketFactory
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.GreenMailUtil
import com.icegreen.greenmail.util.ServerSetupTest
import java.security.Security
import java.time.Instant
import javax.mail.internet.MimeMessage
import javax.sql.DataSource
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetable::class,
        PostUploadTasklet::class,
        IOnIntegrationApplication::class
    ]
)

@TestPropertySource("classpath:application.properties")
@SpringBootTest
internal class PostUploadTaskletTest {

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
    @Autowired
    lateinit var ds: DataSource

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
    fun whenHashIsSuccessFullyInserted_theAssertStatusCompletedAndRecordedHashIsEqualToExpectedHash() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val hr = HashRepositoryImpl(ds)
        val jobId = "PostUploadTest"
        val expectedHash = byteArrayOf(1, 2, 3)
        val jp = initJobParameters(jobId)
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put("file-hash", expectedHash)

        var je = jobLauncherTestUtils.launchStep("PostUpload", jp, se.jobExecution.executionContext)

        val recordedHash = hr.fetchHash(jobId)
        assertEquals(ExitStatus.COMPLETED.exitCode, je.exitStatus.exitCode)
        assertNotNull(recordedHash)
        assertTrue(expectedHash.contentEquals(recordedHash!!))
    }

    @Test
    fun whenNoHashIsOnContext_thenThrowTypeCasExceptionAndAssertNoHashInDb() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val hr = HashRepositoryImpl(ds)
        val jobId = "PostUploadTest2"
        val jp = initJobParameters(jobId)
        val se = utils.createStepExecution()

        var je = jobLauncherTestUtils.launchStep("PostUpload", jp, se.jobExecution.executionContext)

        val ex = je.allFailureExceptions[0]
        assertEquals("TypeCastException", ex::class.java.simpleName)
        assertEquals("null cannot be cast to non-null type kotlin.ByteArray", ex.message)
        assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        val recordedHash = hr.fetchHash(jobId)
        assertNull(recordedHash)
    }
    @Test
    @Sql("insert-hash-post-upload-test.sql")
    fun whenThereIsAlreadyAnHash_ThenMakeSureItWasReplaced() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val hr = HashRepositoryImpl(ds)
        val jobId = "PostUploadTest3"
        val hashBefore = hr.fetchHash(jobId)

        val expectedHash = byteArrayOf(1, 2, 3)
        val jp = initJobParameters(jobId)
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put("file-hash", expectedHash)

        var je = jobLauncherTestUtils.launchStep("PostUpload", jp, se.jobExecution.executionContext)

        val recordedHash = hr.fetchHash(jobId)
        assertEquals(ExitStatus.COMPLETED.exitCode, je.exitStatus.exitCode)
        assertNotNull(recordedHash)
        assertTrue(expectedHash.contentEquals(recordedHash!!))
        assertNotEquals(hashBefore, recordedHash)
    }

    @Test
    fun whenTaskletSucceeds_thenAssertEmailWasSent() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val jobId = "PostUploadEmailSentTest"
        val expectedHash = byteArrayOf(1, 2, 3)
        val jp = initJobParameters(jobId)
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put("file-hash", expectedHash)

        jobLauncherTestUtils.launchStep("PostUpload", jp, se.jobExecution.executionContext)

        val messages: Array<MimeMessage> = testSmtp.receivedMessages
        assertEquals(1, messages.size)
        assertEquals("i-on integration Alert - Job COMPLETED_SUCCESSFULLY", messages[0].subject)
        val e = GreenMailUtil.getBody(messages[0])
        assertTrue(GreenMailUtil.getBody(messages[0]).contains("TestJob COMPLETED_SUCCESSFULLY for file: LEIC_0310.pdf"))
    }

    private fun initJobParameters(jobId: String): JobParameters {
        return JobParametersBuilder()
            .addString("hashKey", "file-hash")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .addString("jobId", jobId)
            .addString("srcRemoteLocation", "https://www.isel.pt/media/uploads/LEIC_0310.pdf")
            .addString("alertRecipient", "client@domain.com")
            .toJobParameters()
    }
}
