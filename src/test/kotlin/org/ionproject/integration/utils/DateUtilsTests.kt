package org.ionproject.integration.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.format.DateTimeParseException

class DateUtilsTests {

    @Test
    fun `when Valid String With Full Date then Return Date`() {
        // Arrange
        val stringFullDate = "11 de maio de 1982"

        // Act
        val date = DateUtils.getDateFrom(stringFullDate)

        // Assert
        assertEquals("1982-05-11", DateUtils.getDateRepresentation(date))
    }

    @Test
    fun `when String With Full Date and Caps then Return Date`() {
        // Arrange
        val stringFullDate = "11 DE MAIO de 1982"

        // Act
        val date = DateUtils.getDateFrom(stringFullDate)

        // Assert
        assertEquals("1982-05-11", DateUtils.getDateRepresentation(date))
    }

    @Test
    fun `when String With Full Date and Two Digit Year then Return Exception`() {
        // Arrange
        val stringFullDate = "11 de maio de 82"

        // Act
        val exception = assertThrows<IllegalArgumentException> {
            DateUtils.getDateFrom(stringFullDate)
        }

        // Assert
        assertEquals("Invalid Year: 82", exception.message)
    }

    @Test
    fun `when String With Full Date and Incorrect Month then Return Exception`() {
        // Arrange
        val stringFullDate = "11 de mai0 de 1982"

        // Act
        val exception = assertThrows<DateTimeParseException> {
            DateUtils.getDateFrom(stringFullDate)
        }

        // Assert
        assertEquals("Text '11 de mai0 de 1982' could not be parsed at index 6", exception.message)
    }
}
