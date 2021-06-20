package org.ionproject.integration.dispatcher

import org.ionproject.integration.infrastructure.error.ArgumentException
import org.ionproject.integration.model.external.calendar.AcademicCalendarDto
import org.ionproject.integration.model.external.timetable.TimetableDto
import org.ionproject.integration.utils.Institution

/**
 * This file contains the interface that must be implemented by each job type.
 * It also contains all the auxiliary data classes and enums.
 */

/**
 * Interface definition
 */
interface IDispatcher {
    fun dispatch(data: ParsedData, filename: String, format: OutputFormat): DispatchResult
}

/**
 * ParsedData will be used to "transport" the final data along with the required metadata.
 * A new implementation should be added for each file type.
 */
sealed class ParsedData(val data: Any) {
    abstract fun getDirectory(repositoryName: String, staging: Filepath): Filepath

    abstract val identifier: String
}

data class TimetableData(
    val programme: ProgrammeMetadata,
    val term: CalendarTerm,
    private val dto: TimetableDto
) : ParsedData(dto) {

    private val PROGRAMMES = "programmes"

    override val identifier: String
        get() = "${javaClass.simpleName}:${programme.acronym}:$term"

    override fun getDirectory(repositoryName: String, staging: Filepath): Filepath {
        val segments = listOf(
            repositoryName,
            programme.institution.domain,
            PROGRAMMES,
            programme.acronym,
            term.toString()
        )

        return staging + segments
    }

    override fun toString(): String = "${javaClass.simpleName}:${programme.acronym}:$term"
}

data class AcademicCalendarData(
    val institution: InstitutionMetadata,
    val academicYear: String,
    private val dto: AcademicCalendarDto
) : ParsedData(dto) {
    companion object Factory {
        private const val ACADEMIC_YEAR_LENGTH = 9
        private const val ACADEMIC_YEARS_FOLDER_NAME = "academic_years"

        fun from(academicCalendarDto: AcademicCalendarDto): AcademicCalendarData =
            AcademicCalendarData(
                InstitutionMetadata(
                    academicCalendarDto.school.name,
                    academicCalendarDto.school.acr,
                    Institution.valueOf(academicCalendarDto.school.acr).identifier
                ),
                academicCalendarDto.terms.first().calendarTerm.take(ACADEMIC_YEAR_LENGTH),
                academicCalendarDto
            )
    }

    override val identifier: String
        get() = "${javaClass.simpleName}:${institution.acronym}:$academicYear"

    override fun getDirectory(repositoryName: String, staging: Filepath): Filepath {
        val segments = listOf(
            repositoryName,
            institution.domain,
            ACADEMIC_YEARS_FOLDER_NAME,
            academicYear
        )

        return staging + segments
    }
}

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
    JSON(".json");

    companion object {
        fun of(name: String): OutputFormat =
            values().firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }
                ?: throw ArgumentException("Invalid format: $name")
    }
}

enum class DispatchResult {
    SUCCESS,
    FAILURE
}

enum class Term(val number: Int) {
    FALL(1),
    SPRING(2)
}
