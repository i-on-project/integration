package org.ionproject.integration.file.implementations

import org.ionproject.integration.infrastructure.PDFBytesFormatChecker
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class BytesFormatCheckerTest {
    private val checker = PDFBytesFormatChecker()

    @Test
    fun `when file is valid PDF then check returns true`() {
        val file = File("src/test/resources/LEIC_example.pdf")
        val result = checker.isValidFormat(file.readBytes())
        assertTrue(result)
    }

    @Test
    fun `when file is valid PDF without extension then check returns true`() {
        val file = File("src/test/resources/pdf_without_extension")
        val result = checker.isValidFormat(file.readBytes())
        assertTrue(result)
    }

    @Test
    fun `when file is not a PDF 2 file then check returns false`() {
        val file = File("src/test/resources/simple_2_0_pdf_file.pdf")
        val result = checker.isValidFormat(file.readBytes())
        assertFalse(result)
    }

    @Test
    fun `when file is not a PDF file then check returns false`() {
        val file = File("src/test/resources/not_a_pdf.txt")
        val result = checker.isValidFormat(file.readBytes())
        assertFalse(result)
    }

    @Test
    fun `when file is not a valid PDF file then check returns false`() {
        val file = File("src/test/resources/fake_pdf.pdf")
        val result = checker.isValidFormat(file.readBytes())
        assertFalse(result)
    }
}
