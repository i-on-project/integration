package org.ionproject.integration.step.chunkbased.reader

import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.extractor.implementations.ITextPdfExtractor
import org.ionproject.integration.extractor.implementations.TabulaPdfExtractor
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.item.ItemReader
import java.nio.file.Path

class ExtractReader(val props: ISELTimetableProperties) : ItemReader<RawData> {

    private lateinit var stepExecution: StepExecution

    @BeforeStep
    fun saveStepExecution(stepExecution: StepExecution) {
        this.stepExecution = stepExecution
    }

    private var nItems: Int = 0

    override fun read(): RawData? {
        if (nItems > 0)
            return null
        val itext = ITextPdfExtractor()
        val tabula = TabulaPdfExtractor()
        val path = stepExecution.jobExecution.executionContext.get(props.pdfKey) as Path

        val headerText = itext.extract(path.toString())
        val tabularText = tabula.extract(path.toString())

        println("header")
        println(headerText.orThrow())
        println("tabula")
        println(tabularText.orThrow())
        props.localFileDestination.toFile().deleteOnExit()
        val rawData = Try.map(headerText, tabularText) { txt, tab -> RawData(tab.first(), txt) }
        nItems += 1
        return rawData.orThrow()
    }
}