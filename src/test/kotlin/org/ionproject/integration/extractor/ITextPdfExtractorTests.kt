package org.ionproject.integration.extractor

import java.io.File
import kotlinx.coroutines.runBlocking
import org.ionproject.integration.extractor.implementations.ITextPdfExtractor
import org.ionproject.integration.utils.Try
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ITextPdfExtractorTests {

    @Test
    fun whenPdfPathIsEmpty_thenReturnExtractionFailed() {
        // Arrange
        val pdfPath = ""
        val result: Try<MutableList<String>>
        val iTextPdfExtractor = ITextPdfExtractor()

        // Act
        result = runBlocking { iTextPdfExtractor.extract(pdfPath) }

        // Assert
        Assertions.assertEquals(true, result is Try.Error)
        Assertions.assertEquals("Empty path", (result as Try.Error).e.message)
    }

    @Test
    fun whenFileDoesntExist_thenReturnExtractionFailed() {
        // Arrange
        val pdfPath = "/invalidpath"
        val result: Try<MutableList<String>>
        val iTextPdfExtractor = ITextPdfExtractor()

        // Act
        result = runBlocking { iTextPdfExtractor.extract(pdfPath) }

        // Assert
        Assertions.assertEquals(true, result is Try.Error)
        Assertions.assertEquals("File doesn't exist or Invalid file format", (result as Try.Error).e.message)
    }

    @Test
    fun whenInvalidFileFormat_thenReturnExtractionFailed() {
        // Arrange
        val file = File.createTempFile("test", ".txt")
        val pdfPath = file.path
        val result: Try<MutableList<String>>
        val iTextPdfExtractor = ITextPdfExtractor()

        // Act
        result = runBlocking { iTextPdfExtractor.extract(pdfPath) }
        file.deleteOnExit()

        // Assert
        Assertions.assertEquals(true, result is Try.Error)
        Assertions.assertEquals("File doesn't exist or Invalid file format", (result as Try.Error).e.message)
    }

    @Test
    fun whenValidFileFormat_thenReturnExtractionSuccessAndJsonData() {
        // Arrange
        val pdfPath = "src/test/resources/test.pdf"
        val result: Try<MutableList<String>>
        val iTextPdfExtractor = ITextPdfExtractor()

        // Act
        result = runBlocking { iTextPdfExtractor.extract(pdfPath) }

        // Assert
        Assertions.assertEquals(true, result is Try.Value<MutableList<String>>)
        Assertions.assertNotEquals(emptyList<String>(), (result as Try.Value<MutableList<String>>).value)
        Assertions.assertEquals(1, result.value.size)
    }
}
