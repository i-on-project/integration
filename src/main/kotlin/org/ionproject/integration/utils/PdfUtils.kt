package org.ionproject.integration.utils

import org.apache.commons.cli.DefaultParser
import org.ionproject.integration.extractor.exceptions.PdfExtractorException
import technology.tabula.CommandLineApp
import java.io.File

object PdfUtils {
    fun processPdf(path: String, vararg tabulaArguments: String): Try<List<String>> {
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
