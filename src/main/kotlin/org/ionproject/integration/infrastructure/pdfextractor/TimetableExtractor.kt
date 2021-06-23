package org.ionproject.integration.infrastructure.pdfextractor

import org.ionproject.integration.infrastructure.PdfUtils
import org.ionproject.integration.utils.Try

object TimetableExtractor {
    // Arguments to pass to tabula
    // -g = Guess the portion of the page to analyze per page
    // -t = Force PDF to be extracted using stream-mode
    // -p = Page range
    // -f = Output format JSON
    // -a top, left, bottom, right
    val ClassSchedule = object : IPdfExtractor {
        override fun extract(pdfPath: String): Try<List<String>> =
            PdfUtils.processPdf(pdfPath, "-a 0,0,670,558", "-g", "-l", "-p", "all", "-f", "JSON")
    }

    val Instructors = object : IPdfExtractor {
        override fun extract(pdfPath: String): Try<List<String>> =
            PdfUtils.processPdf(pdfPath, "-a 671,0,828,558", "-g", "-t", "-p", "all", "-f", "JSON")
    }
}
