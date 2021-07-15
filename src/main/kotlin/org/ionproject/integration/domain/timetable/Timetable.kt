package org.ionproject.integration.domain.timetable

import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.timetable.model.Course
import java.time.ZonedDateTime

data class Timetable(
    var creationDateTime: ZonedDateTime,
    var retrievalDateTime: ZonedDateTime,
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
