package org.ionproject.integration.infrastructure.pdfextractor

import org.ionproject.integration.infrastructure.Try

interface IPdfExtractor {
    /**
     * Extract data from pdf file locate at [pdfPath]
     * @return Try<MutableList<String>>
     *     Success - String list contains extracted data
     *     Failure - PdfExtractorException
     */
    fun extract(pdfPath: String): Try<List<String>>
}
