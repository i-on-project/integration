package org.ionproject.integration.model

import org.ionproject.integration.model.external.timetable.TimetableDto

/**
 * This file contains the interfaces that must be implemented by Dispatcher modules (i.e. FileRepository).
 * It also contains all the auxiliary data classes and enums.
 */
interface IDispatcher<T : ParsedData> {
    fun dispatch(data: T, format: OutputFormat): DispatchResult
}

interface ITimetableDispatcher<TimetableData>

enum class OutputFormat {
    YAML,
    JSON
}

enum class DispatchResult {
    SUCCESS,
    FAILURE
}

sealed class ParsedData(val data: Any)

data class TimetableData(
    val programme: ProgrammeMetadata,
    val term: CalendarTerm,
    private val dto: TimetableDto
) : ParsedData(dto)

data class InstitutionMetadata(
    val name: String,
    val acronym: String,
    val domain: String
)

data class ProgrammeMetadata(
    val institutionMetadata: InstitutionMetadata,
    val name: String,
    val acronym: String
)

data class CalendarTerm(
    val startYear: Int,
    val term: Term
)

enum class Term(val number: Int) {
    FALL(1),
    SPRING(2)
}
