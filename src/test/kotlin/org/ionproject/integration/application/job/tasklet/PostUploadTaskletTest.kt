package org.ionproject.integration.application.job.tasklet

import java.time.Instant
import javax.sql.DataSource
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.application.JobEngine.Companion.JOB_HASH_PARAMETER
import org.ionproject.integration.application.JobEngine.Companion.REMOTE_FILE_LOCATION_PARAMETER
import org.ionproject.integration.application.JobEngine.Companion.TIMESTAMP_PARAMETER
import org.ionproject.integration.application.job.ISELTimetableJob
import org.ionproject.integration.application.job.TIMETABLE_JOB_NAME
import org.ionproject.integration.infrastructure.repository.hash.HashRepositoryImpl
import org.ionproject.integration.application.job.chunkbased.SpringBatchTestUtils
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
        ISELTimetableJob::class,
        PostUploadTasklet::class,
        IOnIntegrationApplication::class
    ]
)

@TestPropertySource("classpath:application.properties")
@SpringBootTest
internal class PostUploadTaskletTest {

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
    lateinit var ds: DataSource

    val utils = SpringBatchTestUtils()

    @Test
    fun `when successful then check file hash is inserted and job is completed`() {
        val hr = HashRepositoryImpl(ds)
        val jobId = "PostUploadTest"
        val expectedHash = byteArrayOf(1, 2, 3)
        val jp = initJobParameters(jobId)
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put("file-hash", expectedHash)

        val je = jobLauncherTestUtils.launchStep("PostUpload", jp, se.jobExecution.executionContext)

        val recordedHash = hr.fetchHash(jobId)
        assertEquals(ExitStatus.COMPLETED.exitCode, je.exitStatus.exitCode)
        assertNotNull(recordedHash)
        assertTrue(expectedHash.contentEquals(recordedHash!!))
    }

    @Test
    fun `when file hash doesnt exist then throw exception and assert hash isn't inserted in the database`() {
        val hr = HashRepositoryImpl(ds)
        val jobId = "PostUploadTest2"
        val jp = initJobParameters(jobId)
        val se = utils.createStepExecution()

        val je = jobLauncherTestUtils.launchStep("PostUpload", jp, se.jobExecution.executionContext)

        val ex = je.allFailureExceptions[0]
        assertEquals("NullPointerException", ex::class.java.simpleName)
        assertEquals("null cannot be cast to non-null type kotlin.ByteArray", ex.message)
        assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        val recordedHash = hr.fetchHash(jobId)
        assertNull(recordedHash)
    }

    @Test
    @Sql("insert-hash-post-upload-test.sql")
    fun `when file hash already exists then it is replaced`() {
        val hr = HashRepositoryImpl(ds)
        val jobId = "PostUploadTest3"
        val hashBefore = hr.fetchHash(jobId)

        val expectedHash = byteArrayOf(1, 2, 3)
        val jp = initJobParameters(jobId)
        val se = utils.createStepExecution()
        se.jobExecution.executionContext.put("file-hash", expectedHash)

        val je = jobLauncherTestUtils.launchStep("PostUpload", jp, se.jobExecution.executionContext)

        val recordedHash = hr.fetchHash(jobId)
        assertEquals(ExitStatus.COMPLETED.exitCode, je.exitStatus.exitCode)
        assertNotNull(recordedHash)
        assertTrue(expectedHash.contentEquals(recordedHash!!))
        assertNotEquals(hashBefore, recordedHash)
    }

    private fun initJobParameters(jobId: String): JobParameters {
        return JobParametersBuilder()
            .addString("hashKey", "file-hash")
            .addLong(TIMESTAMP_PARAMETER, Instant.now().toEpochMilli())
            .addString(JOB_HASH_PARAMETER, jobId)
            .addString(REMOTE_FILE_LOCATION_PARAMETER, "https://www.isel.pt/media/uploads/LEIC_0310.pdf")
            .addString("alertRecipient", "client@domain.com")
            .toJobParameters()
    }
}
