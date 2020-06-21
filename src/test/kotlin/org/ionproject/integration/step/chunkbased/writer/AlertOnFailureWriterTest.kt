package org.ionproject.integration.step.chunkbased.writer

import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.utils.Try
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetable::class,
        BatchAutoConfiguration::class,
        IOnIntegrationApplication::class]
)
@TestPropertySource(
    properties = [
        "isel-timetable.alertRecipient=org.ionproject@gmail.com",
        "ion.core-base-url = test",
        "ion.core-token = test",
        "ion.core-request-timeout-seconds = 1"
    ]
)
@SpringBatchTest
internal class AlertOnFailureWriterTest {

    @Test
    fun whenValueIsTrue_ThenDoNotThrow() {
        // Arrange
        val writer = AlertOnFailureWriter()
        val processed = mutableListOf(Try.ofValue(true))

        // Act & Assert
        assertDoesNotThrow { writer.write(processed) }
    }

    @Test
    fun whenValueIsException_ThenAssertThrows() {
        // Arrange
        val msg = "The timetable header has changed its format"
        val ex = PdfExtractorException(msg)
        val writer = AlertOnFailureWriter()
        val processed = mutableListOf(Try.ofError<PdfExtractorException>(ex))
        // Act & Assert
        val actualEx = assertThrows<PdfExtractorException> { writer.write(processed) }
        assertEquals(msg, actualEx.message)
        assertEquals("PdfExtractorException", actualEx.javaClass.simpleName)
    }
}
