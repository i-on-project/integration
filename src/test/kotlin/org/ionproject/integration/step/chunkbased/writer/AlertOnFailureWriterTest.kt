package org.ionproject.integration.step.chunkbased.writer

import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.alert.implementations.EmailAlertChannel
import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.utils.Try
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.validateMockitoUsage
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetable::class,
        ISELTimetableProperties::class,
        BatchAutoConfiguration::class,
        IOnIntegrationApplication::class]
)
@TestPropertySource(
    properties = [
        "isel-timetable.alertRecipient=org.ionproject@gmail.com"
    ]
)
@SpringBatchTest
internal class AlertOnFailureWriterTest {
    @Autowired
    private lateinit var props: ISELTimetableProperties

    @Test
    fun whenValueIsTrue_ThenDoNotThrow() {
        // Arrange
        val alertChannel = Mockito.mock(EmailAlertChannel::class.java)
        Mockito.`when`(alertChannel.sendSuccessAlert())
            .thenReturn(Try.ofValue(true))
        val spyAlert = Mockito.spy(alertChannel)
        val writer = AlertOnFailureWriter(props, spyAlert)
        val processed = mutableListOf(Try.ofValue(true))

        // Act & Assert
        assertDoesNotThrow { writer.write(processed) }
        Mockito.verify(spyAlert).sendSuccessAlert()
    }

    @Test
    fun whenValueIsException_ThenSendEmail() {
        // Arrange
        val alertChannel = Mockito.mock(EmailAlertChannel::class.java)
        val msg = "The timetable header has changed its format"
        val ex = PdfExtractorException(msg)
        Mockito.`when`(alertChannel.sendFailureAlert(ex))
            .thenReturn(Try.ofValue(true))
        val spyAlert = Mockito.spy(alertChannel)
        val writer = AlertOnFailureWriter(props, spyAlert)
        val processed = mutableListOf(Try.ofError<PdfExtractorException>(ex))
        // Act & Assert
        val actualEx = assertThrows<PdfExtractorException> { writer.write(processed) }
        assertEquals(msg, actualEx.message)
        assertEquals("PdfExtractorException", actualEx.javaClass.simpleName)
        Mockito.verify(spyAlert).sendFailureAlert(ex)
    }

    @AfterEach
    fun validate() {
        // Shows errors on the method they occurred in, and not the next time the framework is called
        validateMockitoUsage()
    }
}
