package org.ionproject.integration.extractor.implementations

import com.itextpdf.kernel.pdf.PdfDate
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import org.ionproject.integration.extractor.interfaces.IPdfExtractor
import org.ionproject.integration.utils.Try
import java.text.SimpleDateFormat
import java.util.TimeZone

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
                    for (i in 1..pdfDoc.numberOfPages)
                        data.add(
                            PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)) +
                                getCreationDateFromPdfDocument(pdfDoc)
                        )
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
        val creationDateCalendar = PdfDate.decode(creationDateString)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmssX")
        return dateFormat.format(creationDateCalendar.time)
    }
}
