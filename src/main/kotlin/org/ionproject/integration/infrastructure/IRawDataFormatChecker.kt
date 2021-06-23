package org.ionproject.integration.infrastructure

import org.ionproject.integration.domain.timetable.RawTimetableData
import org.ionproject.integration.utils.Try

interface IRawDataFormatChecker {
    fun checkFormat(rawTimetableData: RawTimetableData): Try<Boolean>
}
