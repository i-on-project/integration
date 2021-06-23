package org.ionproject.integration.model

import org.ionproject.integration.domain.common.Weekday
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class WeekdayTests {

    @Test
    fun `when given portuguese monday then parse correctly`() {
        val expected = Weekday.MONDAY
        val source = listOf("segunda", "segunda feira", "segunda-feira", "seGunda-FEIRA")

        assertAllMatchWeekday(source, expected)
    }

    @Test
    fun `when given portuguese tuesday then parse correctly`() {
        val expected = Weekday.TUESDAY
        val source = listOf("terça", "terça feira", "terca feira", "teRcA-FEirA")

        assertAllMatchWeekday(source, expected)
    }

    @Test
    fun `when given portuguese wednesday then parse correctly`() {
        val expected = Weekday.WEDNESDAY
        val source = listOf("quarta", "quarta feira", "quarta-feira", "QUARTa-FeiRa")

        assertAllMatchWeekday(source, expected)
    }

    @Test
    fun `when given portuguese thursday then parse correctly`() {
        val expected = Weekday.THURSDAY
        val source = listOf("quinta", "quinta feira", "quinta-feira", "qUinTa-FeiRa")

        assertAllMatchWeekday(source, expected)
    }

    @Test
    fun `when given portuguese friday then parse correctly`() {
        val expected = Weekday.FRIDAY
        val source = listOf("sexta", "sexta feira", "sexta-feira", "sExta-FeiRa")

        assertAllMatchWeekday(source, expected)
    }

    @Test
    fun `when given portuguese saturday then parse correctly`() {
        val expected = Weekday.SATURDAY
        val source = listOf("sábado", "sabado", "sÁbadO")

        assertAllMatchWeekday(source, expected)
    }

    @Test
    fun `when given portuguese sunday then parse correctly`() {
        val expected = Weekday.SUNDAY
        val source = listOf("domingo", "Domingo", "DOMINGO")

        assertAllMatchWeekday(source, expected)
    }

    @Test
    fun `when given an invalid portuguese weekday then fail`() {
        val source = listOf("na", "Domingo")

        assertThrows<IllegalArgumentException> { assertAllMatchWeekday(source, Weekday.SUNDAY) }
    }

    private fun assertAllMatchWeekday(source: List<String>, weekday: Weekday) {
        source.forEach { text ->
            val parsed = Weekday.fromPortuguese(text)
            assertEquals(weekday, parsed)
        }
    }
}
