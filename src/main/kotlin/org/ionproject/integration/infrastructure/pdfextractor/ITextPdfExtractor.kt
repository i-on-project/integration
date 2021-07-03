package org.ionproject.integration.infrastructure.pdfextractor

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import org.ionproject.integration.infrastructure.DateUtils
import org.ionproject.integration.infrastructure.Try
import org.ionproject.integration.infrastructure.exception.PdfExtractorException
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.io.path.readAttributes

class ITextPdfExtractor : IPdfExtractor {
    /**
     * Extract text data from pdf file locate at [pdfPath]
     * @return PdfExtractorException in case of any error
     */
    override fun extract(pdfPath: String): Try<List<String>> {
        if (pdfPath.isEmpty()) return Try.ofError<PdfExtractorException>(PdfExtractorException("Empty path"))

        val pdfReader = Try.of { PdfReader(pdfPath) }

        try {
            val data = mutableListOf<String>()

            return pdfReader
                .map { reader -> PdfDocument(reader) }
                .map { pdfDoc ->
                    for (i in 1..pdfDoc.numberOfPages) {
                        val pageData = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i))
                        data.add(pageData)
                    }
                    data.add(getCreationDateFromPdfDocument(pdfPath))
                }
                .map { data.toList() }
                .mapError { PdfExtractorException("Itext cannot process file") }
        } finally {
            pdfReader
                .map { reader -> reader.close() }
        }
    }

    private fun getCreationDateFromPdfDocument(pdfPath: String): String {
        val creationDate =
            Path.of(pdfPath)
                .readAttributes<BasicFileAttributes>(LinkOption.NOFOLLOW_LINKS)
                .creationTime()
        return DateUtils.formatToISO8601(
            ZonedDateTime.ofInstant(
                creationDate.toInstant(),
                ZoneId.systemDefault()
            )
        )
    }
}
