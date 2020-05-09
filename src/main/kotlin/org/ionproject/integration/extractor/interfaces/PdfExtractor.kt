package org.ionproject.integration.extractor.interfaces

import org.ionproject.integration.utils.Try

interface PdfExtractor {
    /**
     * Extract data from pdf file locate at [pdfPath]
     * @return Try<MutableList<String>>
     *     Success - String list contains extracted data
     *     Failure - PdfExtractorException
     */
    suspend fun extract(pdfPath: String): Try<MutableList<String>>
}
