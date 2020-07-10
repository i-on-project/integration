package org.ionproject.integration.builder.implementations

import java.nio.file.Paths
import org.ionproject.integration.model.internal.generic.AcademicCalendar
import org.ionproject.integration.model.internal.generic.Term
import org.ionproject.integration.utils.YAMLException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AcademicCalendarFactoryTest {
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
        assertEquals("2019-12-23", interruptions[0].startDate)
        assertEquals("2020-01-04", interruptions[0].endDate)
        assertEquals(3, evaluations.size)
        assertEquals("Exames época normal", evaluations[0].name)
        assertEquals("2020-01-13", evaluations[0].startDate)
        assertEquals("2020-02-01", evaluations[0].endDate)
        assertEquals("Exames época recurso", evaluations[1].name)
        assertEquals("2020-02-03", evaluations[1].startDate)
        assertEquals("2020-02-15", evaluations[1].endDate)
        assertEquals("Exames época especial", evaluations[2].name)
        assertEquals("2020-02-26", evaluations[2].startDate)
        assertEquals("2020-03-07", evaluations[2].endDate)
        assertEquals(1, others.size)
        assertEquals("Divulgação de horários", others[0].name)
        assertEquals("2019-07-22", others[0].startDate)
        assertEquals("2019-07-22", others[0].endDate)
        assertEquals(2, td.size)
        assertEquals("Turmas 1º Semestre", td[0].name)
        assertEquals(1, td[0].curricularTerm.size)
        assertEquals(1, td[0].curricularTerm[0].id)
        assertEquals("2019-09-16", td[0].startDate)
        assertEquals("2020-01-11", td[0].endDate)
        assertEquals("Turmas excepto 1º Semestre", td[1].name)
        assertEquals(5, td[1].curricularTerm.size)
        assertEquals("2019-09-09", td[1].startDate)
        assertEquals("2020-12-21", td[1].endDate)
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
