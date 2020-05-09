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
     * @return Try<MutableList<String>>
     *     Success - String list contains all extracted data in json format
     *     Failure - PdfExtractorException
     */
    override fun extract(pdfPath: String): Try<MutableList<String>> {
        if (pdfPath.isEmpty()) return Try.ofError(PdfExtractorException("Empty path"))

        val pdfFile = File(pdfPath)

        if (!pdfFile.exists()) return Try.ofError(PdfExtractorException("File doesn't exist"))

        // Arguments to pass to tabula
        // -g = Guess the portion of the page to analyze per page
        // -l = Force PDF to be extracted using lattice-mode extraction
        // -p = Page range
        // -f = Output format JSON
        val args = arrayOf(pdfFile.absolutePath, "-g", "-l", "-p", "all", "-f", "JSON")
        val parser = DefaultParser()
        val cmd = parser.parse(CommandLineApp.buildOptions(), args)

        val data = StringBuilder()
        var result = Try.of { CommandLineApp(data, cmd).extractTables(cmd) }

        return if (result is Try.Error) {
            Try.ofError(PdfExtractorException("Tabula cannot process file"))
        } else Try.of(mutableListOf(data.toString()))
    }
}
