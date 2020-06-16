package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import java.io.File
import java.lang.reflect.UndeclaredThrowableException
import java.time.Instant
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.file.exceptions.InvalidFormatException
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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
        DownloadAndCompareTasklet::class,
        IOnIntegrationApplication::class
    ]
)
@SpringBatchTest
@SpringBootTest
internal class DownloadAndCompareTaskletDownloadSuccessful{

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    val utils = SpringBatchTestUtils()

    @Test
    fun whenTaskletIsSuccessful_ThenAssertPathIsInContextAndFileExists() {
        val pathKey = "pdf-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters()
        val file = File("src/test/resources/TIMETABLE-SUCCESSFUL.pdf")
        val expectedPath = file.toPath()
        try {
            
            val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)

            assertEquals(ExitStatus.COMPLETED.exitCode, je.exitStatus.exitCode)
            assertEquals(expectedPath, je.executionContext[pathKey])
            assertTrue(file.exists())
        } finally {
            file.deleteOnExit()
        }
    }
    @Test
    @Sql("insert-timetable-pdf-hash.sql")
    fun whenHashIsSameAsRecorded_ThenThrowDownloadAndCompareTaskletException() {
        val pathKey = "pdf-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters()
        val file = File("src/test/resources/TIMETABLE-SAME-AS-RECORDED.pdf")
        val expectedPath = file.toPath()
        try {

            val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)
            val ex = (je.allFailureExceptions[0] as UndeclaredThrowableException).undeclaredThrowable

            assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
            assertEquals(expectedPath, je.executionContext[pathKey])
            assertTrue(file.exists())
            assertEquals("DownloadAndCompareTaskletException", ex::class.java.simpleName)
        } finally {
            file.deleteOnExit()
        }
    }

    private fun initJobParameters(): JobParameters {
        return JobParametersBuilder()
            .addString("pdfKey", "pdf-path")
            .addString("hashKey", "file-hash")
            .addString("localFileDestination", "src/test/resources/TIMETABLE-SAME-AS-RECORDED.pdf")
            .addString("pdfRemoteLocation", "https://www.isel.pt/media/uploads/LEIC_0310.pdf")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .addString("jobId", "2")
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
            .addString("pdfKey", "pdf-path")
            .addString("hashKey", "file-hash")
            .addString("localFileDestination", "src/test/resources/TIMETABLE.pdf")
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
@SpringBatchTest
internal class DownloadAndCompareTaskletUrlNotPdfTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    val utils = SpringBatchTestUtils()

    @Test
    fun whenUrlIsNotPdf_ThenAssertExceptionIsInvalidFormatAndPathIsNotIncludedInContext() {
        val localFileDestination = "src/test/resources/NOT-USED.pdf"
        val pathKey = "pdf-path"
        val ec = utils.createExecutionContext()
        val jp = initJobParameters()
        val file = File(localFileDestination)
        try {
            val je = jobLauncherTestUtils.launchStep("Download And Compare", jp, ec)
            val actualPath = je.executionContext.get(pathKey)
            val ex = je.allFailureExceptions[0] as UndeclaredThrowableException
            assertEquals("InvalidFormatException", ex.undeclaredThrowable::class.java.simpleName)
            val iFEx = ex.undeclaredThrowable as InvalidFormatException
            assertEquals("Downloaded content was not in the PDF format.", iFEx.message)
            assertNull(actualPath)
            assertFalse(file.exists())
        } finally {
            file.deleteOnExit()
        }
    }

    private fun initJobParameters(): JobParameters {
        return JobParametersBuilder()
            .addString("pdfKey", "pdf-path")
            .addString("hashKey", "file-hash")
            .addString("localFileDestination", "src/test/resources/NOT-USED.pdf")
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
            assertEquals("ServerErrorException", ute.undeclaredThrowable::class.java.simpleName)
            assertEquals("Server responded with error code 500", ute.undeclaredThrowable.message)
            assertFalse(file.exists())
        } finally {
            file.deleteOnExit()
        }
    }

    private fun initJobParameters(): JobParameters {
        return JobParametersBuilder()
            .addString("pdfKey", "pdf-path")
            .addString("hashKey", "file-hash")
            .addString("localFileDestination", "src/test/resources/SERVER_DOWN.pdf")
            .addString("pdfRemoteLocation", "http://httpstat.us/500")
            .addLong("timestamp", Instant.now().toEpochMilli())
            .toJobParameters()
    }
}
