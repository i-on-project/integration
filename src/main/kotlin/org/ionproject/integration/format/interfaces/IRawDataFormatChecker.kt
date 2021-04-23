package org.ionproject.integration.format.interfaces

import org.ionproject.integration.model.internal.timetable.isel.RawTimetableData
import org.ionproject.integration.utils.Try

interface IRawDataFormatChecker {
    fun checkFormat(rawTimetableData: RawTimetableData): Try<Boolean>
}
