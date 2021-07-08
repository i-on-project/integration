package org.ionproject.integration.application.dto

import org.ionproject.integration.domain.calendar.AcademicCalendarDto
import org.ionproject.integration.domain.common.Term
import org.ionproject.integration.domain.evaluations.EvaluationsDto
import org.ionproject.integration.domain.timetable.dto.TimetableDto
import org.ionproject.integration.infrastructure.file.Filepath

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

        fun from(academicCalendarDto: AcademicCalendarDto, identifier: String): AcademicCalendarData =
            AcademicCalendarData(
                InstitutionMetadata(
                    academicCalendarDto.school.name,
                    academicCalendarDto.school.acr,
                    identifier
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

    data class EvaluationsData(
        val programme: ProgrammeMetadata,
        val term: CalendarTerm,
        private val dto: EvaluationsDto
    ) : ParsedData(dto) {
        companion object Factory {
            private const val ACADEMIC_YEAR_LENGTH = 9
            private const val ACADEMIC_YEARS_FOLDER_NAME = "academic_years"

            fun from(evaluationsDto: EvaluationsDto, identifier: String): EvaluationsData =
                EvaluationsData(
                    ProgrammeMetadata(
                        InstitutionMetadata(
                            evaluationsDto.school.name,
                            evaluationsDto.school.acr,
                            identifier
                        ),
                        evaluationsDto.programme.name,
                        evaluationsDto.programme.acr
                    ),
                    CalendarTerm(
                        evaluationsDto.calendarTerm.take(4).toInt(),
                        when (evaluationsDto.calendarTerm.takeLast(1).toInt()) {
                            1 -> Term.FALL
                            2 -> Term.SPRING
                            else -> throw IllegalArgumentException("Invalid Term ${evaluationsDto.calendarTerm}")
                        }
                    ),
                    evaluationsDto
                )
        }

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
    }
}
