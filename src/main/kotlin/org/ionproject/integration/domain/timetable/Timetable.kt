package org.ionproject.integration.domain.timetable

import org.ionproject.integration.domain.timetable.model.Course
import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.infrastructure.text.RegexUtils

private const val CALENDAR_TERM_REGEX = """\b(\s?Ano\sLetivo\s?:\s?)(\d{2,4}\/\d{2,4})\s?-\s?(Verão|Inverno)\b"""
private const val YEAR_OFFSET = 2000 // Add when terms are given with only two digits (i.e. 20/21 instead of 2020/2021)

data class Timetable(
    var creationDateTime: String = "",
    var retrievalDateTime: String = "",
    var school: School = School(),
    var programme: Programme = Programme(),
    var calendarTerm: String = "",
    var calendarSection: String = "",
    var curricularTerm: Int = 0,
    var courses: List<Course> = listOf()
)

private val curricularTerm = """\b\D+(\d)\w+\b""".toRegex()

fun getCurricularTermFromSection(section: String): Int {
    val match = curricularTerm.find(section) ?: throw IllegalArgumentException(section)
    val (term) = match.destructured

    return term.toInt()
}

fun getCalendarTerm(rawCalendar: String): String {
    if (!RegexUtils.isMatch(CALENDAR_TERM_REGEX, rawCalendar))
        throw IllegalArgumentException("Invalid calendar term: $rawCalendar")

    val (_, _, termYears, termSemester) = RegexUtils.findMatches(CALENDAR_TERM_REGEX, rawCalendar)

    val (startYear, endYear) = getYearsFromText(termYears)
    val termNumber = getTermNumberFromSemesterText(termSemester)

    return "$startYear-$endYear-$termNumber"
}

private fun getYearsFromText(data: String): Pair<Int, Int> {
    val (first, second) = data.split("/")
        .map(String::toInt)
        .map { if (it < YEAR_OFFSET) it + YEAR_OFFSET else it }

    return first to second
}

private fun getTermNumberFromSemesterText(semesterText: String): Int = when (semesterText) {
    "Inverno" -> 1
    "Verão" -> 2
    else -> throw IllegalArgumentException("Invalid term description: $semesterText")
}
