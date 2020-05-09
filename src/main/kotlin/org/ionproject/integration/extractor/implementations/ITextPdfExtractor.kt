package org.ionproject.integration.extractor.implementations

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import org.ionproject.integration.extractor.interfaces.PdfExtractor
import org.ionproject.integration.utils.Try

class ITextPdfExtractor : PdfExtractor {
    /**
     * Extract text data from pdf file locate at [pdfPath]
     * @return Try<MutableList<String>>
     *     Success - String list contains all extracted data in text format
     *     Failure - PdfExtractorException
     */
    override fun extract(pdfPath: String): Try<MutableList<String>> {
        if (pdfPath.isEmpty()) return Try.ofError(PdfExtractorException("Empty path"))

        val tryReader = Try.of { PdfReader(pdfPath) }
        if (tryReader is Try.Error) return Try.ofError(PdfExtractorException("File doesn't exist or Invalid file format"))

        val reader = (tryReader as Try.Value<PdfReader>).value
        val pdfDoc = PdfDocument(reader)

        val data = mutableListOf<String>()
        for (i in 1..pdfDoc.numberOfPages) {
            val result = Try.of { PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)) }

            if (result is Try.Error) return Try.ofError(PdfExtractorException("Itext cannot process file"))

            data.add((result as Try<String>).toString())
        }

        reader.close()

        return Try.of(data)
    }
}
