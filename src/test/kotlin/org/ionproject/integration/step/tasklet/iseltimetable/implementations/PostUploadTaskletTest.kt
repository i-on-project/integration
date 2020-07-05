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
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
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
@SpringBatchTest
@SpringBootTest
internal class PostUploadTaskletTest {

    @Autowired
    lateinit var jobLauncherTestUtils: JobLauncherTestUtils

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
        assertTrue(GreenMailUtil.getBody(messages[0]).contains("ISEL Timetable Batch Job COMPLETED_SUCCESSFULLY for file: LEIC_0310.pdf"))
    }

    private fun initJobParameters(jobId: String): JobParameters {
        return JobParametersBuilder()
            .addString("hashKey", "file-hash")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .addString("jobId", jobId)
            .addString("pdfRemoteLocation", "https://www.isel.pt/media/uploads/LEIC_0310.pdf")
            .addString("alertRecipient", "client@domain.com")
            .toJobParameters()
    }
}
