package org.ionproject.integration.extractor.implementations

import java.io.File
import org.apache.commons.cli.DefaultParser
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import org.ionproject.integration.extractor.interfaces.PdfExtractor
import org.ionproject.integration.utils.Try
import technology.tabula.CommandLineApp

class TabulaPdfExtractor : PdfExtractor {
    /**
     * Extract table data from pdf file locate at [pdfPath]
     * @return CompositeException with list of exceptions in case of any error
     */
    override fun extract(pdfPath: String): Try<MutableList<String>> {
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
        return Try.of { arrayOf(pdfFile.absolutePath, "-g", "-l", "-p", "all", "-f", "JSON") }
            .map { args -> parser.parse(CommandLineApp.buildOptions(), args) }
            .map { cmd -> CommandLineApp(data, cmd).extractTables(cmd) }
            .map { mutableListOf(data.toString()) }
            .mapError { PdfExtractorException("Tabula cannot process file") }
    }
}
