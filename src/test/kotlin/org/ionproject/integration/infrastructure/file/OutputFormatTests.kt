package org.ionproject.integration.infrastructure.file

import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OutputFormatTests {
    @Test
    fun `when given JSON format then assert extension is correct`() {
        val expected = ".json"
        val actual = OutputFormat.JSON.extension

        assertEquals(expected, actual)
    }

    @Test
    fun `when given YAML format then assert extension is correct`() {
        val expected = ".yml"
        val actual = OutputFormat.YAML.extension

        assertEquals(expected, actual)
    }

    @Test
    fun `when given json in factory method then return JSON format`() {
        val expected = OutputFormat.JSON
        val actual = OutputFormat.of("json")

        assertEquals(expected, actual)
    }

    @Test
    fun `when given json in uppercase in factory method then return JSON format`() {
        val expected = OutputFormat.JSON
        val actual = OutputFormat.of("JSON")

        assertEquals(expected, actual)
    }

    @Test
    fun `when given json with leading or trailing space in factory method then return JSON format`() {
        val expected = OutputFormat.JSON
        val actual = OutputFormat.of("   JsON   ")

        assertEquals(expected, actual)
    }

    @Test
    fun `when given yaml in factory method then return JSON format`() {
        val expected = OutputFormat.YAML
        val actual = OutputFormat.of("yaml")

        assertEquals(expected, actual)
    }

    @Test
    fun `when given yml in factory method then return JSON format`() {
        val expected = OutputFormat.YAML
        val actual = OutputFormat.of("yml")

        assertEquals(expected, actual)
    }

    @Test
    fun `when given YAML in uppercase in factory method then return JSON format`() {
        val expected = OutputFormat.YAML
        val actual = OutputFormat.of("YAML")

        assertEquals(expected, actual)
    }

    @Test
    fun `when given YML in uppercase in factory method then return JSON format`() {
        val expected = OutputFormat.YAML
        val actual = OutputFormat.of("YML")

        assertEquals(expected, actual)
    }

    @Test
    fun `when given YAML with leading or trailing space in factory method then return JSON format`() {
        val expected = OutputFormat.YAML
        val actual = OutputFormat.of("   YaMl   ")

        assertEquals(expected, actual)
    }

    @Test
    fun `when given YML with leading or trailing space in factory method then return JSON format`() {
        val expected = OutputFormat.YAML
        val actual = OutputFormat.of("   YMl   ")

        assertEquals(expected, actual)
    }

    @Test
    fun `when given empty format then error`() {
        val expectedErrorMessage = INVALID_FORMAT_ERROR.format("")
        val ex = assertThrows<ArgumentException> {
            OutputFormat.of("")
        }

        assertEquals(expectedErrorMessage, ex.message)
    }

    @Test
    fun `when given unsupported format then error`() {
        val expectedErrorMessage = INVALID_FORMAT_ERROR.format("csv")
        val ex = assertThrows<ArgumentException> {
            OutputFormat.of("csv")
        }

        assertEquals(expectedErrorMessage, ex.message)
    }
}
