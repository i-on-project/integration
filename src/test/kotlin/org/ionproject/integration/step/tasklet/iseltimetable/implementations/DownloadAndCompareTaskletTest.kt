package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import java.io.File
import java.lang.reflect.UndeclaredThrowableException
import java.time.Instant
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.step.tasklet.iseltimetable.exceptions.DownloadAndCompareTaskletException
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.ionproject.integration.utils.CompositeException
import org.junit.FixMethodOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runners.MethodSorters
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
        DownloadAndCompareTasklet::class,
        IOnIntegrationApplication::class
    ]
)

@TestPropertySource(
    properties = [
        "ion.core-base-url = test",
        "ion.core-token = test",
        "ion.core-request-timeout-seconds = 1",
        "ion.resources-folder=src/test/resources/"
    ]
)
@SpringBatchTest
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
internal class DownloadAndCompareTaskletDownloadSuccessfulButHashTheSameAsRecorded {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    val utils = SpringBatchTestUtils()

    @Test
    fun whenTaskletIsSuccessful_ThenAssertPathIsInContextAndFileExists() {
        val pathKey = "pdf-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters("1")
        val file = File("src/test/resources/LEIC_0310.pdf")
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
        val pathKey = "pdf-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters("2")
        val file = File("src/test/resources/LEIC_0310.pdf")
        val expectedPath = file.toPath()
        try {

            val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)

            assertEquals(ExitStatus.STOPPED.exitCode, je.exitStatus.exitCode)
            assertEquals(expectedPath, je.executionContext[pathKey])
            assertTrue(file.exists())
        } finally {
            file.delete()
        }
    }

    private fun initJobParameters(jobId: String): JobParameters {
        return JobParametersBuilder()
            .addString("pdfRemoteLocation", "https://www.isel.pt/media/uploads/LEIC_0310.pdf")
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
@TestPropertySource(
    properties = [
        "ion.core-base-url = test",
        "ion.core-token = test",
        "ion.core-request-timeout-seconds = 1",
        "ion.resources-folder=src/test/resources/"
    ]
)
@SpringBatchTest
internal class DownloadAndCompareTaskletMissingPropertiesTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    val utils = SpringBatchTestUtils()

    @Test
    fun whenUrlIsNotDefined_ThenReturnsIllegalArgumentExceptionAndPathIsNotIncludedInContext() {
        val localFileDestination = "src/test/resources/TIMETABLE.pdf"
        val pathKey = "pdf-path"
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
            .addString("pdfRemoteLocation", "")
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
@TestPropertySource(
    properties = [
        "ion.core-base-url = test",
        "ion.core-token = test",
        "ion.core-request-timeout-seconds = 1",
        "ion.resources-folder=src/test/resources/"
    ]
)
@SpringBatchTest
internal class DownloadAndCompareTaskletUrlNotPdfTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    val utils = SpringBatchTestUtils()

    @Test
    fun whenUrlIsNotPdf_ThenAssertExceptionIsInvalidFormatAndPathIsNotIncludedInContext() {
        val localFileDestination = "src/test/resources"
        val pathKey = "pdf-path"
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
            .addString("pdfRemoteLocation", "https://kotlinlang.org/")
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
@TestPropertySource(
    properties = [
        "ion.core-base-url = test",
        "ion.core-token = test",
        "ion.core-request-timeout-seconds = 1",
        "ion.resources-folder=src/test/resources/"
    ]
)
@SpringBatchTest
internal class DownloadAndCompareTaskletServerErrorTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    val utils = SpringBatchTestUtils()

    @Test
    fun whenServerResponds5xx_ThenAssertExceptionIsServerErrorAndPathIsNotInContext() {
        val localFileDestination = "src/test/resources/SERVER_DOWN.pdf"
        val pathKey = "pdf-path"
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

    private fun initJobParameters(): JobParameters {
        return JobParametersBuilder()
            .addString("pdfRemoteLocation", "http://httpstat.us/500")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .toJobParameters()
    }
}
