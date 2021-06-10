package org.ionproject.integration.extractor.implementations

import com.itextpdf.kernel.pdf.PdfDate
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import org.ionproject.integration.extractor.interfaces.IPdfExtractor
import org.ionproject.integration.utils.DateUtils
import org.ionproject.integration.utils.Try
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar

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
                    data.add(getCreationDateFromPdfDocument(pdfDoc))
                }
                .map { data.toList() }
                .mapError { PdfExtractorException("Itext cannot process file") }
        } finally {
            pdfReader
                .map { reader -> reader.close() }
        }
    }

    private fun getCreationDateFromPdfDocument(pdfDocument: PdfDocument): String {
        val creationDateString = pdfDocument.documentInfo.getMoreInfo(PdfName.CreationDate.value)
        val creationDateCalendar = PdfDate.decode(creationDateString) ?: Calendar.getInstance()

        return DateUtils.formatToISO8601(
            ZonedDateTime.ofInstant(
                creationDateCalendar.toInstant(),
                ZoneId.systemDefault()
            )
        )
    }
}
