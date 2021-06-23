package org.ionproject.integration.application.dispatcher

import org.ionproject.integration.application.dto.ParsedData
import org.ionproject.integration.infrastructure.file.OutputFormat

interface IDispatcher {
    fun dispatch(data: ParsedData, filename: String, format: OutputFormat): DispatchResult
}

enum class DispatchResult {
    SUCCESS,
    FAILURE
}
