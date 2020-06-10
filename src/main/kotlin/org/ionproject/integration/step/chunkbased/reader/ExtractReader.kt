package org.ionproject.integration.step.chunkbased.reader

import java.nio.file.Path
import org.ionproject.integration.extractor.implementations.ITextPdfExtractor
import org.ionproject.integration.extractor.implementations.TabulaPdfExtractor
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component("ExtractReader")
@StepScope
class ExtractReader : ItemReader<RawData> {

    private lateinit var stepExecution: StepExecution

    @BeforeStep
    fun saveStepExecution(stepExecution: StepExecution) {
        this.stepExecution = stepExecution
    }

    @Value("#{jobParameters['pdfKey']}")
    private lateinit var pdfKey: String

    @Value("#{jobParameters['hashKey']}")
    private lateinit var hashKey: String

    private var nItems: Int = 0

    override fun read(): RawData? {
        if (nItems > 0)
            return null

        val itext = ITextPdfExtractor()
        val tabula = TabulaPdfExtractor()

        val path = stepExecution.jobExecution.executionContext.get(pdfKey) as Path
        stepExecution.jobExecution.executionContext.put(hashKey, path.toFile().hashCode())

        val headerText = itext.extract(path.toString())
        val tabularText = tabula.extract(path.toString())

        path.toFile().delete()
        val rawData = Try.map(headerText, tabularText) { txt, tab -> RawData(tab.first(), txt) }

        nItems += 1

        return rawData.orThrow()
    }
}
