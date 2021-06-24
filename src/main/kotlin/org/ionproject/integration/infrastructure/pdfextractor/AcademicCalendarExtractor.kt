package org.ionproject.integration.infrastructure.pdfextractor

import org.ionproject.integration.infrastructure.Try

object AcademicCalendarExtractor {
    // Arguments to pass to tabula
    // -g = Guess the portion of the page to analyze per page
    // -t = Force PDF to be extracted using stream-mode
    // -p = Page range
    // -f = Output format JSON
    // -a top, left, bottom, right

    val calendarTable = object : IPdfExtractor {
        override fun extract(pdfPath: String): Try<List<String>> =
            PdfUtils.processPdf(pdfPath, "-g", "-l", "-p", "all", "-f", "JSON")
    }
}
