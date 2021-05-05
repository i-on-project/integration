package org.ionproject.integration.dispatcher

import org.ionproject.integration.model.external.timetable.TimetableDto

/**
 * This file contains the interfaces that must be implemented by Dispatcher modules (i.e. FileRepository).
 * It also contains all the auxiliary data classes and enums.
 */

/**
 * Interface definitions
 */
sealed interface IDispatcher<T : ParsedData> {
    fun dispatch(data: T, format: OutputFormat): DispatchResult
}

interface ITimetableDispatcher : IDispatcher<TimetableData>

/**
 * ParsedData will be used to "transport" the final data along with the required metadata.
 * A new implementation should be added for each file type and matching IDispatcher interface.
 */
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
    val institution: InstitutionMetadata,
    val name: String,
    val acronym: String
)

data class CalendarTerm(
    val startYear: Int,
    val term: Term
) {
    override fun toString(): String = "$startYear-${startYear + 1}-${term.number}"
}

enum class OutputFormat(val extension: String) {
    YAML(".yml"),
    JSON(".json")
}

enum class DispatchResult {
    SUCCESS,
    FAILURE
}

enum class Term(val number: Int) {
    FALL(1),
    SPRING(2)
}
