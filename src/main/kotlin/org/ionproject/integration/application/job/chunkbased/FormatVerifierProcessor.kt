package org.ionproject.integration.application.job.chunkbased

import org.ionproject.integration.application.job.ISELTimetableJob
import org.ionproject.integration.domain.timetable.IRawDataFormatChecker
import org.ionproject.integration.domain.timetable.dto.RawTimetableData
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
