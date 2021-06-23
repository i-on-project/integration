package org.ionproject.integration.utils

import org.ionproject.integration.application.JobResult
import org.ionproject.integration.infrastructure.exceptions.AlertConfigurationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class EmailUtilsTest {

    @Test
    fun whenConfigurationIsSuccessfulAndJobIsCompleted_thenAssertConfsAreFilled() {
        // Arrange
        val email = "client@domain.com"
        val jobName = "test job"
        val result = JobResult.COMPLETED_SUCCESSFULLY
        val asset = "file001.doc"
        // Act
        val conf =
            EmailUtils.configure(jobName, result, email, asset, null)

        // Assert
        assertEquals(1, conf.recipients.size)
        assertEquals(email, conf.recipients[0].toUnicodeString())
        assertEquals("i-on integration Alert - Job ${result.name}", conf.subject)
        assertEquals("$jobName COMPLETED_SUCCESSFULLY for file: $asset", conf.text)
    }
    @Test
    fun whenConfigurationIsSuccessfulAndJobIsFailed_thenAssertConfsAreFilled() {
        // Arrange
        val email = "client@domain.com"
        val jobName = "test job"
        val result = JobResult.FAILED
        val asset = "file001.doc"
        val message = "error x"
        // Act
        val conf =
            EmailUtils.configure(jobName, result, email, asset, message)

        // Assert
        assertEquals(1, conf.recipients.size)
        assertEquals(email, conf.recipients[0].toUnicodeString())
        assertEquals("i-on integration Alert - Job ${result.name}", conf.subject)
        assertEquals("$jobName FAILED for file: $asset with message $message", conf.text)
    }
    @Test
    fun whenInvalidEmailIsProvided_thenAssertAlertConfigurationIsThrown() {
        // Arrange
        val invalidEmail = "client@domain"
        val jobName = "test job"
        val result = JobResult.FAILED
        val asset = "file001.doc"
        val message = "error x"
        // Act
        val ex =
            assertThrows<AlertConfigurationException> { EmailUtils.configure(jobName, result, invalidEmail, asset, message) }
        assertEquals("There is an invalid e-mail address in the list of recipients", ex.message)
    }
}
