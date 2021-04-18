package org.ionproject.integration.extractor.implementations

import java.io.File
import org.apache.commons.cli.DefaultParser
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import org.ionproject.integration.extractor.interfaces.IPdfExtractor
import org.ionproject.integration.utils.Try
import technology.tabula.CommandLineApp

class TimetableExtractor : IPdfExtractor {
    /**
     * Extract table data from pdf file locate at [pdfPath]
     * @return PdfExtractorException in case of any error
     */
    override fun extract(pdfPath: String): Try<List<String>> {
        if (pdfPath.isEmpty()) return Try.ofError<PdfExtractorException>(PdfExtractorException("Empty path"))

        val pdfFile = File(pdfPath)

        if (!pdfFile.exists()) return Try.ofError<PdfExtractorException>(PdfExtractorException("File doesn't exist"))

        val parser = DefaultParser()
        val data = StringBuilder()

        // Arguments to pass to tabula
        // -g = Guess the portion of the page to analyze per page
        // -l = Force PDF to be extracted using lattice-mode extraction
        // -p = Page range
        // -f = Output format JSON
        // -a 72,36,828,558
        // -a top, left, bottom, right
        return Try.of { arrayOf(pdfFile.absolutePath, "-a 0,0,670,558", "-g", "-l", "-p", "all", "-f", "JSON") }
            .map { args -> parser.parse(CommandLineApp.buildOptions(), args) }
            .map { cmd -> CommandLineApp(data, cmd).extractTables(cmd) }
            .map { listOf(data.toString()) }
            .mapError { PdfExtractorException("Tabula cannot process file") }
    }
}

class InstructorExtractor : IPdfExtractor {
    /**
     * Extract table data from pdf file locate at [pdfPath]
     * @return PdfExtractorException in case of any error
     */
    override fun extract(pdfPath: String): Try<List<String>> {
        if (pdfPath.isEmpty()) return Try.ofError<PdfExtractorException>(PdfExtractorException("Empty path"))

        val pdfFile = File(pdfPath)

        if (!pdfFile.exists()) return Try.ofError<PdfExtractorException>(PdfExtractorException("File doesn't exist"))

        val parser = DefaultParser()
        val data = StringBuilder()

        // Arguments to pass to tabula
        // -g = Guess the portion of the page to analyze per page
        // -l = Force PDF to be extracted using stream-mode
        // -p = Page range
        // -f = Output format JSON
        // -a top, left, bottom, right
        return Try.of { arrayOf(pdfFile.absolutePath, "-a 671,0,828,558", "-g", "-t", "-p", "all", "-f", "JSON") }
            .map { args -> parser.parse(CommandLineApp.buildOptions(), args) }
            .map { cmd -> CommandLineApp(data, cmd).extractTables(cmd) }
            .map { listOf(data.toString()) }
            .mapError { PdfExtractorException("Tabula cannot process file") }
    }
}
