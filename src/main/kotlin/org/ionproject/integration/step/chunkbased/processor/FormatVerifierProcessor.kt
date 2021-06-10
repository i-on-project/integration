package org.ionproject.integration.step.chunkbased.processor

import org.ionproject.integration.format.interfaces.IRawDataFormatChecker
import org.ionproject.integration.job.ISELTimetableJob
import org.ionproject.integration.model.internal.timetable.isel.RawTimetableData
import org.ionproject.integration.utils.Try
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class FormatVerifierProcessor(var state: ISELTimetableJob.State, private val formatVerifier: IRawDataFormatChecker) :
    ItemProcessor<RawTimetableData, Try<Boolean>> {
    override fun process(item: RawTimetableData): Try<Boolean>? {
        state.rawTimetableData = item
        return formatVerifier.checkFormat(item)
    }
}
