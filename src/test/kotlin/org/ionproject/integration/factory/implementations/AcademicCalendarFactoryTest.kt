package org.ionproject.integration.factory.implementations

import java.nio.file.Paths
import java.time.Instant
import java.util.Date
import org.ionproject.integration.model.internal.generic.AcademicCalendar
import org.ionproject.integration.model.internal.generic.Term
import org.ionproject.integration.utils.exceptions.YAMLException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AcademicCalendarFactoryTest {
    private fun dateFromString(string: String): Date {
        val inst = Instant.parse(string)
        return Date.from(inst)
    }
    @Test
    fun whenFileIsAcademicCalendar_thenParseSuccessfully() {
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased",
            "/generic/academic-calendar/academic-calendar.yml"
        )
        val f = AcademicCalendarFactory()
        val ac = f.parse(path) as AcademicCalendar
        assertions(ac)
    }

    private fun assertions(ac: AcademicCalendar) {
        val school = ac.school
        val academicYear = ac.academicYear
        val terms = ac.terms
        assertEquals("A", school.acr)
        assertEquals("Escola A", school.name)
        assertEquals("2019/2020", academicYear)
        assertEquals(1, terms.size)
        termAssertions(terms[0])
    }

    private fun termAssertions(term: Term) {
        val name = term.name
        val interruptions = term.interruptions
        val evaluations = term.evaluations
        val td = term.details
        val others = term.otherEvents
        assertEquals("Semestre de Inverno", name)
        assertEquals(1, interruptions.size)
        assertEquals("Férias de Natal", interruptions[0].name)
        assertEquals(dateFromString("2019-12-23T00:00:00.000Z"), interruptions[0].startDate)
        assertEquals(dateFromString("2020-01-04T00:00:00.000Z"), interruptions[0].endDate)
        assertEquals(3, evaluations.size)
        assertEquals("Exames época normal", evaluations[0].name)
        assertEquals(dateFromString("2020-01-13T00:00:00.000Z"), evaluations[0].startDate)
        assertEquals(dateFromString("2020-02-01T00:00:00.000Z"), evaluations[0].endDate)
        assertEquals("Exames época recurso", evaluations[1].name)
        assertEquals(dateFromString("2020-02-03T00:00:00.000Z"), evaluations[1].startDate)
        assertEquals(dateFromString("2020-02-15T00:00:00.000Z"), evaluations[1].endDate)
        assertEquals("Exames época especial", evaluations[2].name)
        assertEquals(dateFromString("2020-02-26T00:00:00.000Z"), evaluations[2].startDate)
        assertEquals(dateFromString("2020-03-07T00:00:00.000Z"), evaluations[2].endDate)
        assertEquals(1, others.size)
        assertEquals("Divulgação de horários", others[0].name)
        assertEquals(dateFromString("2019-07-22T00:00:00.000Z"), others[0].startDate)
        assertEquals(dateFromString("2019-07-22T00:00:00.000Z"), others[0].endDate)
        assertEquals(2, td.size)
        assertEquals("Turmas 1º Semestre", td[0].name)
        assertEquals(1, td[0].curricularTerm.size)
        assertEquals(1, td[0].curricularTerm[0].id)
        assertEquals(dateFromString("2019-09-16T00:00:00.000Z"), td[0].startDate)
        assertEquals(dateFromString("2020-01-11T00:00:00.000Z"), td[0].endDate)
        assertEquals("Turmas excepto 1º Semestre", td[1].name)
        assertEquals(5, td[1].curricularTerm.size)
        assertEquals(dateFromString("2019-09-09T00:00:00.000Z"), td[1].startDate)
        assertEquals(dateFromString("2020-12-21T00:00:00.000Z"), td[1].endDate)
    }

    @Test
    fun whenFileIsNotAcademicCalendar_thenThrowYamlException() {
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased",
            "/generic/exam-schedule/exam-schedule.yml"
        )
        val f = AcademicCalendarFactory()
        val ye = assertThrows<YAMLException> { f.parse(path) }
        assertEquals("Invalid yaml", ye.message)
    }
    @Test
    fun whenYamlIsIncomplete_thenThrowsYamlException() {
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased",
            "/generic/academic-calendar/incomplete-academic-calendar.yml"
        )
        val f = AcademicCalendarFactory()
        val ye = assertThrows<YAMLException> { f.parse(path) }
        assertEquals("Invalid yaml", ye.message)
    }
}
