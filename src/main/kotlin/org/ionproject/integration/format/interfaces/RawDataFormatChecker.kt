package org.ionproject.integration.format.interfaces

import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.Try

interface RawDataFormatChecker {
    fun checkFormat(rawData: RawData): Try<Boolean>
}