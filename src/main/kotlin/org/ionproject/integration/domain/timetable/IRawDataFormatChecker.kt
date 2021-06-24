package org.ionproject.integration.domain.timetable

import org.ionproject.integration.domain.timetable.dto.RawTimetableData
import org.ionproject.integration.infrastructure.Try

interface IRawDataFormatChecker {
    fun checkFormat(rawTimetableData: RawTimetableData): Try<Boolean>
}
