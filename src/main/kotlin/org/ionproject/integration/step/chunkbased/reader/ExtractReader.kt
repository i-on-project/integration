package org.ionproject.integration.step.chunkbased.reader

import java.nio.file.Path
import org.ionproject.integration.extractor.implementations.ITextPdfExtractor
import org.ionproject.integration.extractor.implementations.TabulaPdfExtractor
import org.ionproject.integration.file.implementations.FileDigestImpl
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component

@Component("ExtractReader")
@StepScope
class ExtractReader : ItemReader<RawData> {

    private lateinit var stepExecution: StepExecution

    @BeforeStep
    fun saveStepExecution(stepExecution: StepExecution) {
        this.stepExecution = stepExecution
    }

    private var nItems: Int = 0

    override fun read(): RawData? {
        if (nItems > 0)
            return null
        val path = stepExecution.jobExecution.executionContext.get("pdf-path") as Path
        try {
            val itext = ITextPdfExtractor()
            val tabula = TabulaPdfExtractor()
            val fd = FileDigestImpl()

            val headerText = itext.extract(path.toString())
            val tabularText = tabula.extract(path.toString())

            path.toFile().deleteOnExit()
            val rawData = Try.map(headerText, tabularText) { txt, tab -> RawData(tab.first(), txt) }
                .orThrow()

            val fileHash = fd.digest(path.toFile())
            stepExecution.jobExecution.executionContext.put("file-hash", fileHash)

            nItems += 1

            return rawData
        } finally {
            path.toFile().delete()
        }
    }
}
