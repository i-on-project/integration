package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.file.exceptions.InvalidFormatException
import org.ionproject.integration.file.exceptions.ServerErrorException
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.batch.core.StepContribution
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [ISELTimetable::class, DownloadAndCompareTasklet::class, BatchAutoConfiguration::class, IOnIntegrationApplication::class])
@TestPropertySource(properties = ["local-file-destination=/tmp/TIMETABLE-SUCCESSFUL.pdf", "pdf-remote-location:https://www.isel.pt/media/uploads/LEIC_0310.pdf"])
internal class DownloadAndCompareTaskletTestSuccessFul {

    @Autowired
    private lateinit var downloadAndCompareTasklet: DownloadAndCompareTasklet

    @Test
    fun whenTaskletIsSuccessful_ThenAssertPathIsInContextAndFileExists() {
        val localFileDestination = "/tmp/TIMETABLE-SUCCESSFUL.pdf"
        val pathKey = "pdf-path"

        val contribution = Mockito.mock(StepContribution::class.java)
        val chunkContext = SpringBatchTestUtils().createChunkContext()
        val file = File(localFileDestination)
        val expectedPath = Paths.get(file.path)
        try {
            downloadAndCompareTasklet.execute(contribution, chunkContext)
            val actualPath = chunkContext.stepContext.stepExecution.jobExecution.executionContext.get(pathKey) as Path
            assertTrue(file.exists())
            assertEquals(actualPath, expectedPath)
        } finally {
            file.deleteOnExit()
        }
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [ISELTimetable::class, DownloadAndCompareTasklet::class, BatchAutoConfiguration::class, IOnIntegrationApplication::class])
internal class DownloadAndCompareTaskletMissingPropertiesTest {

    @Autowired
    private lateinit var downloadAndCompareTasklet: DownloadAndCompareTasklet

    @Test
    fun whenUrlIsNotDefined_ThenReturnsIllegalArgumentExceptionAndPathIsNotIncludedInContext() {
        val localFileDestination = "/tmp/TIMETABLE.pdf"
        val pathKey = "pdf-path"
        val contribution = Mockito.mock(StepContribution::class.java)
        val chunkContext = SpringBatchTestUtils().createChunkContext()
        val file = File(localFileDestination)
        try {
            val repeatStatus = Try.of { downloadAndCompareTasklet.execute(contribution, chunkContext) }
            assertThrows<IllegalArgumentException> { repeatStatus.orThrow() }
            assertFalse(file.exists())
            val actualPath = chunkContext.stepContext.stepExecution.jobExecution.executionContext.get(pathKey)
            assertNull(actualPath)
        } finally {
            file.deleteOnExit()
        }
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [ISELTimetable::class, DownloadAndCompareTasklet::class, BatchAutoConfiguration::class, IOnIntegrationApplication::class])
@TestPropertySource(properties = ["local-file-destination=/tmp/NOT-USED.pdf", "pdf-remote-location=https://kotlinlang.org/"])
internal class DownloadAndCompareTaskletUrlNotPdfTest {

    @Autowired
    private lateinit var downloadAndCompareTasklet: DownloadAndCompareTasklet

    @Test
    fun whenUrlIsNotPdf_ThenAssertExceptionIsInvalidFormatAndPathIsNotIncludedInContext() {
        val localFileDestination = "/tmp/NOT-USED.pdf"
        val pathKey = "pdf-path"
        val contribution = Mockito.mock(StepContribution::class.java)
        val chunkContext = SpringBatchTestUtils().createChunkContext()
        val file = File(localFileDestination)
        try {
            val repeatStatus = Try.of { downloadAndCompareTasklet.execute(contribution, chunkContext) }
            val ex = assertThrows<InvalidFormatException> { repeatStatus.orThrow() }
            val actualPath = chunkContext.stepContext.stepExecution.jobExecution.executionContext.get(pathKey)
            assertEquals("Downloaded content was not in the PDF format.", ex.message)
            assertNull(actualPath)
            assertFalse(file.exists())
        } finally {
            file.deleteOnExit()
        }
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [ISELTimetable::class, DownloadAndCompareTasklet::class, BatchAutoConfiguration::class, IOnIntegrationApplication::class])
@TestPropertySource(properties = ["local-file-destination=/tmp/SERVER_DOWN.pdf", "pdf-remote-location=http://httpstat.us/500"])
internal class DownloadAndCompareTaskletServerErrorTest {

    @Autowired
    private lateinit var downloadAndCompareTasklet: DownloadAndCompareTasklet

    @Test
    fun whenServerResponds5xx_ThenAssertExceptionIsServerErrorAndPathIsNotInContext() {
        val localFileDestination = "/tmp/SERVER_DOWN.pdf"
        val pathKey = "pdf-path"
        val contribution = Mockito.mock(StepContribution::class.java)
        val chunkContext = SpringBatchTestUtils().createChunkContext()
        val file = File(localFileDestination)
        try {
            val repeatStatus = Try.of { downloadAndCompareTasklet.execute(contribution, chunkContext) }
            val ex = assertThrows<ServerErrorException> { repeatStatus.orThrow() }
            val actualPath = chunkContext.stepContext.stepExecution.jobExecution.executionContext.get(pathKey)
            assertEquals("Server responded with error code 500", ex.message)
            assertNull(actualPath)
            assertFalse(file.exists())
        } finally {
            file.deleteOnExit()
        }
    }
}
