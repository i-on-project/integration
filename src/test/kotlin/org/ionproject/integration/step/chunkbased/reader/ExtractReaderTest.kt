package org.ionproject.integration.step.chunkbased.reader

import java.io.File
import java.nio.file.Paths
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.ionproject.integration.utils.CompositeException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
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
        ExtractReader::class,
        BatchAutoConfiguration::class,
        IOnIntegrationApplication::class]
)
@TestPropertySource(
    properties = [
        "isel-timetable.localFileDestination=src/test/resources/sample.pdf",
        "isel-timetable.pdfKey=pdf-path"
    ]
)
@SpringBatchTest
internal class ExtractReaderTestSuccess {
    @Autowired
    private lateinit var props: ISELTimetableProperties

    @Autowired
    private lateinit var reader: ExtractReader

    @Test
    fun whenFileIsSuccessfullyRead_thenRawDataIsFilled() {
        // Arrange
        File("src/test/resources/test.pdf").copyTo(props.localFileDestination.toFile())
        val chunkContext = SpringBatchTestUtils().createChunkContext()
        val stepExecution = chunkContext.stepContext.stepExecution
        stepExecution.jobExecution.executionContext.put(props.pdfKey, props.localFileDestination)

        // Act
        reader.saveStepExecution(stepExecution)
        val rd = reader.read() as RawData

        // Assert
        assertEquals("[]", rd.jsonData)
        assertEquals(1, rd.textData.size)
        assertTrue(!props.localFileDestination.toFile().exists())
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetable::class,
        ExtractReader::class,
        BatchAutoConfiguration::class,
        IOnIntegrationApplication::class]
)
@TestPropertySource(
    properties = [
        "isel-timetable.localFileDestination=src/test/resources/invalid.pdf",
        "isel-timetable.pdfKey=pdf-path"
    ]
)
@SpringBatchTest
internal class ExtractReaderTestUnexistingFile {
    @Autowired
    private lateinit var props: ISELTimetableProperties

    @Autowired
    private lateinit var reader: ExtractReader

    @Test
    fun whenNoFileDoesntExist_thenThrowPdfExtractorException() {
        // Arrange
        val chunkContext = SpringBatchTestUtils().createChunkContext()
        val stepExecution = chunkContext.stepContext.stepExecution
        stepExecution.jobExecution.executionContext.put(props.pdfKey, props.localFileDestination)

        // Act
        reader.saveStepExecution(stepExecution)
        val rd = assertThrows<CompositeException> { reader.read() }

        // Assert
        assertEquals("PdfExtractorException", rd.exceptions[1].javaClass.simpleName)
        assertTrue(!props.localFileDestination.toFile().exists())
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        ISELTimetable::class,
        ExtractReader::class,
        BatchAutoConfiguration::class,
        IOnIntegrationApplication::class]
)
@TestPropertySource(
    properties = [
        "isel-timetable.localFileDestination=",
        "isel-timetable.pdfKey=pdf-path"
    ]
)
@SpringBatchTest
internal class ExtractReaderTestPdfPathEmtpy {
    @Autowired
    private lateinit var props: ISELTimetableProperties

    @Autowired
    private lateinit var reader: ExtractReader

    @Test
    fun whenPdfPathIsEmpty_thenReturnExtractionFailed() {
        // Arrange
        val chunkContext = SpringBatchTestUtils().createChunkContext()
        val stepExecution = chunkContext.stepContext.stepExecution
        stepExecution.jobExecution.executionContext.put(props.pdfKey, Paths.get(""))

        // Act
        reader.saveStepExecution(stepExecution)
        val ex = assertThrows<CompositeException> { reader.read() as RawData }

        // Assert
        assertEquals("PdfExtractorException", ex.exceptions[0].javaClass.simpleName)
        assertEquals("Empty path", ex.exceptions[0].message)
    }
}
