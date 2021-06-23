package org.ionproject.integration.application.job.chunkbased.reader

import java.nio.file.Path
import org.ionproject.integration.infrastructure.pdfextractor.ITextPdfExtractor
import org.ionproject.integration.infrastructure.pdfextractor.TimetableExtractor
import org.ionproject.integration.infrastructure.FileDigestImpl
import org.ionproject.integration.domain.timetable.RawTimetableData
import org.ionproject.integration.utils.Try.Companion.map
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component

@Component("ExtractReader")
@StepScope
class ExtractReader : ItemReader<RawTimetableData> {

    private lateinit var stepExecution: StepExecution

    @BeforeStep
    fun saveStepExecution(stepExecution: StepExecution) {
        this.stepExecution = stepExecution
    }

    private var nItems: Int = 0

    override fun read(): RawTimetableData? {
        if (nItems > 0)
            return null
        val path = stepExecution.jobExecution.executionContext.get("file-path") as Path
        try {
            val itext = ITextPdfExtractor()
            val fd = FileDigestImpl()

            val headerText = itext.extract(path.toString())
            val tabularText = TimetableExtractor.ClassSchedule.extract(path.toString())
            val instructors = TimetableExtractor.Instructors.extract(path.toString())

            val rawData = map(headerText, tabularText, instructors) { (text, tabular, instructors) ->
                RawTimetableData(
                    tabular.first(),
                    text.dropLast(1),
                    instructors.first(),
                    text.last()
                )
            }.orThrow()

            val fileHash = fd.digest(path.toFile())
            stepExecution.jobExecution.executionContext.put("file-hash", fileHash)

            nItems += 1

            return rawData
        } finally {
            path.toFile().delete()
        }
    }
}
