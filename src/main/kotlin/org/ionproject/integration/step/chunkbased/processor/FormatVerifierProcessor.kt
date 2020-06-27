package org.ionproject.integration.step.chunkbased.processor

import org.ionproject.integration.format.interfaces.IRawDataFormatChecker
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.Try
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class FormatVerifierProcessor(var state: ISELTimetable.State, private val formatVerifier: IRawDataFormatChecker) :
    ItemProcessor<RawData, Try<Boolean>> {
    override fun process(item: RawData): Try<Boolean>? {
        state.rawData = item
        return formatVerifier.checkFormat(item)
    }
}
