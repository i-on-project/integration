package org.ionproject.integration.extractor.implementations

import java.io.File
import org.apache.commons.cli.DefaultParser
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import org.ionproject.integration.extractor.interfaces.IPdfExtractor
import org.ionproject.integration.utils.Try
import technology.tabula.CommandLineApp

object TimetableExtractor {
    // Arguments to pass to tabula
    // -g = Guess the portion of the page to analyze per page
    // -t = Force PDF to be extracted using stream-mode
    // -p = Page range
    // -f = Output format JSON
    // -a top, left, bottom, right
    val ClassSchedule = object : IPdfExtractor {
        override fun extract(pdfPath: String): Try<List<String>> =
            processPdf(pdfPath, "-a 0,0,670,558", "-g", "-l", "-p", "all", "-f", "JSON")
    }

    val Instructors = object : IPdfExtractor {
        override fun extract(pdfPath: String): Try<List<String>> =
            processPdf(pdfPath, "-a 671,0,828,558", "-g", "-t", "-p", "all", "-f", "JSON")
    }

    private fun processPdf(path: String, vararg tabulaArguments: String): Try<List<String>> {
        if (path.isEmpty()) return Try.ofError<PdfExtractorException>(PdfExtractorException("Empty path"))
        val pdfFile = File(path)
        if (!pdfFile.exists()) return Try.ofError<PdfExtractorException>(PdfExtractorException("File doesn't exist"))

        val parser = DefaultParser()
        val data = StringBuilder()

        return Try.of { arrayOf(pdfFile.absolutePath, *tabulaArguments) }
            .map { args -> parser.parse(CommandLineApp.buildOptions(), args) }
            .map { cmd -> CommandLineApp(data, cmd).extractTables(cmd) }
            .map { listOf(data.toString()) }
            .mapError { PdfExtractorException("Tabula cannot process file") }
    }
}
