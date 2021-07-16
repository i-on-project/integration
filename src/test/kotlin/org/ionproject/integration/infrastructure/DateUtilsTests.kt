package org.ionproject.integration.infrastructure

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
        assertEquals("1982-05-11", DateUtils.formatToCalendarDate(date))
    }

    @Test
    fun `when String With Full Date and Caps then Return Date`() {
        // Arrange
        val stringFullDate = "11 DE MAIO de 1982"

        // Act
        val date = DateUtils.getDateFrom(stringFullDate)

        // Assert
        assertEquals("1982-05-11", DateUtils.formatToCalendarDate(date))
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

    @Test
    fun `when String Has Valid Range of Dates then Return True`() {
        // Arrange
        val stringDateRange = "11 de maio a 17 de maio de 1982"

        // Act and Assert
        assertTrue(DateUtils.isDateRange(stringDateRange))
    }

    @Test
    fun `when String Has Invalid Range of Dates then Return False`() {
        // Arrange
        val stringDateRange = "11 de maio até 17 de maio de 1982"

        // Act and Assert
        assertFalse(DateUtils.isDateRange(stringDateRange))
    }

    @Test
    fun `when String Has Valid Range of Dates then Return Date List`() {
        // Arrange
        val stringDateRange = "11 de maio a 17 de maio de 1982"

        // Act
        val intervalDate = DateUtils.getDateRange(stringDateRange)

        // Assert
        assertEquals("1982-05-11", DateUtils.formatToCalendarDate(intervalDate.from))
        assertEquals("1982-05-17", DateUtils.formatToCalendarDate(intervalDate.to))
    }

    @Test
    fun `when String Has Valid Range of Dates with Years then Return Date List`() {
        // Arrange
        val stringDateRange = "11 de maio de 1982 a 17 de maio de 1983"

        // Act
        val intervalDate = DateUtils.getDateRange(stringDateRange)

        // Assert
        assertEquals("1982-05-11", DateUtils.formatToCalendarDate(intervalDate.from))
        assertEquals("1983-05-17", DateUtils.formatToCalendarDate(intervalDate.to))
    }

    @Test
    fun `when String Has Invalid Range of Dates then Return Exception`() {
        // Arrange
        val stringDateRange = "11 de maio ao 17 de maio de 1982"

        // Act
        val exception = assertThrows<DateTimeParseException> {
            DateUtils.getDateRange(stringDateRange)
        }

        // Assert
        assertEquals("Text '11 de maio ao 17 de maio de 1982' could not be parsed at index 11", exception.message)
    }

    @Test
    fun `when String Has a Single Date then Return List with Same Date`() {
        // Arrange
        val stringDateRange = "11 de maio de 1982"

        // Act
        val intervalDate = DateUtils.getDateRange(stringDateRange)

        // Assert
        assertEquals("1982-05-11", DateUtils.formatToCalendarDate(intervalDate.from))
        assertEquals("1982-05-11", DateUtils.formatToCalendarDate(intervalDate.to))
    }

    @Test
    fun `when String Has Two Days and Single Month then Return List with Dates`() {
        // Arrange
        val stringDateRange = "11 e 12 de maio de 1982"

        // Act
        val intervalDate = DateUtils.getDateRange(stringDateRange)

        // Assert
        assertEquals("1982-05-11", DateUtils.formatToCalendarDate(intervalDate.from))
        assertEquals("1982-05-12", DateUtils.formatToCalendarDate(intervalDate.to))
    }

    @Test
    fun `when String Has Day Range and Single Month then Return List with Dates`() {
        // Arrange
        val stringDateRange = "11 a 17 de maio de 1982"

        // Act
        val intervalDate = DateUtils.getDateRange(stringDateRange)

        // Assert
        assertEquals("1982-05-11", DateUtils.formatToCalendarDate(intervalDate.from))
        assertEquals("1982-05-17", DateUtils.formatToCalendarDate(intervalDate.to))
    }

    @Test
    fun `when Date Time Range and Duration are presented then Return a IntervalDateTime`() {
        // Arrange
        val dayMonthStr = "29 jun. (terça)"
        val timeStr = "19h00"
        val durationStr = "3h00"

        // Act
        val intervalDate = DateUtils.getEvaluationDateTimeFrom("2021", dayMonthStr, timeStr, durationStr, Zone.Portugal)

        // Assert
        assertEquals("2021-06-29T18:00Z[UTC]", intervalDate.from.toString())
        assertEquals("2021-06-29T21:00Z[UTC]", intervalDate.to.toString())
    }

    @Test
    fun `when Date Time Range with and Duration with Half-Hour are presented then Return a IntervalDateTime`() {
        // Arrange
        val dayMonthStr = "2 jun. (segunda)"
        val timeStr = "19h30"
        val durationStr = "2h30"

        // Act
        val intervalDate = DateUtils.getEvaluationDateTimeFrom("2021", dayMonthStr, timeStr, durationStr, Zone.Portugal)

        // Assert
        assertEquals("2021-06-02T18:30Z[UTC]", intervalDate.from.toString())
        assertEquals("2021-06-02T21:00Z[UTC]", intervalDate.to.toString())
    }
}
