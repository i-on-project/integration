package org.ionproject.integration.step.chunkbased.processor

import org.ionproject.integration.format.implementations.ISELTimetableFormatChecker
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.Try
import org.springframework.batch.item.ItemProcessor

class FormatVerifierProcessor(var state: ISELTimetable.State) : ItemProcessor<RawData, Try<Boolean>> {
    override fun process(item: RawData): Try<Boolean>? {
        val formatVerifier = ISELTimetableFormatChecker()

        state.rawData = item
        return formatVerifier.checkFormat(item)
    }
}