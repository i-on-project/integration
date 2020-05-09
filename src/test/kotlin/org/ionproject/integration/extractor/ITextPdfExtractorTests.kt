package org.ionproject.integration.extractor

import java.io.File
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import org.ionproject.integration.extractor.implementations.ITextPdfExtractor
import org.ionproject.integration.utils.CompositeException
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ITextPdfExtractorTests {

    @Test
    fun whenPdfPathIsEmpty_thenReturnExtractionFailed() {
        // Arrange
        val pdfPath = ""
        val iTextPdfExtractor = ITextPdfExtractor()

        // Act
        val result = iTextPdfExtractor.extract(pdfPath)

        // Assert
        val e = assertThrows<PdfExtractorException> { result.orThrow() }
        assertEquals("Empty path", e.message)
    }

    @Test
    fun whenFileDoesntExist_thenReturnExtractionFailed() {
        // Arrange
        val pdfPath = "/invalidpath"
        val iTextPdfExtractor = ITextPdfExtractor()

        // Act
        val result = iTextPdfExtractor.extract(pdfPath)

        // Assert
        val e = assertThrows<CompositeException> { result.orThrow() }
        assertEquals(2, e.exceptions.count())
        assertEquals(true, e.exceptions[1] is PdfExtractorException)
        assertEquals("Itext cannot process file", e.exceptions[1].message)
    }

    @Test
    fun whenInvalidFileFormat_thenReturnExtractionFailed() {
        // Arrange
        val file = File.createTempFile("test", ".txt")
        val pdfPath = file.path
        val iTextPdfExtractor = ITextPdfExtractor()

        // Act
        val result = iTextPdfExtractor.extract(pdfPath)
        file.deleteOnExit()

        // Assert
        val e = assertThrows<CompositeException> { result.orThrow() }
        assertEquals(2, e.exceptions.count())
        assertEquals(true, e.exceptions[1] is PdfExtractorException)
        assertEquals("Itext cannot process file", e.exceptions[1].message)
    }

    @Test
    fun whenValidFileFormat_thenReturnExtractionSuccessAndJsonData() {
        // Arrange
        val pdfPath = "src/test/resources/test.pdf"
        val iTextPdfExtractor = ITextPdfExtractor()

        // Act
        val result = iTextPdfExtractor.extract(pdfPath)
            .orThrow()

        // Assert
        assertEquals(1, result.count())
    }
}
