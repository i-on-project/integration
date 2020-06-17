package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import java.time.Instant
import javax.sql.DataSource
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
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
@SpringBatchTest
@SpringBootTest
internal class PostUploadTaskletTest {

    @Autowired
    lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    lateinit var ds: DataSource

    val utils = SpringBatchTestUtils()

    @Test
    fun whenHashIsSuccessFullyInserted_theAssertStatusCompletedAndRecordedHashIsEqualToExpectedHash() {
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

    private fun initJobParameters(jobId: String): JobParameters {
        return JobParametersBuilder()
            .addString("hashKey", "file-hash")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .addString("jobId", jobId)
            .toJobParameters()
    }
}
