package org.ionproject.integration.builder.implementations

import org.ionproject.integration.model.internal.generic.ExamSchedule
import org.ionproject.integration.utils.YAMLException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Paths
import java.time.Instant
import java.util.Date

internal class ExamScheduleFactoryTest {
    private fun dateFromString(string: String): Date {
        val inst = Instant.parse(string)
        return Date.from(inst)
    }

    @Test
    fun whenFileIsValidYaml_thenParseSuccessfully() {
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased/generic/exam-schedule/exam-schedule.yml"
        )
        val f = ExamScheduleFactory()
        val ec = f.parse(path) as ExamSchedule
        assertions(ec)
    }

    private fun assertions(ec: ExamSchedule) {
        val ay = ec.academicYear
        val school = ec.school
        val p = ec.programme
        val exs = ec.exams
        assertEquals("School A", school.name)
        assertEquals("A", school.acr)
        assertEquals("2019/2020", ay)
        assertEquals("Programme A", p.name)
        assertEquals("PA", p.acr)
        assertEquals(6, exs.size)
        assertEquals("AED", exs[0].name)
        assertEquals(dateFromString("2020-07-09T14:00:00.000Z"), exs[0].date)
        assertEquals(null, exs[0].location)
        assertEquals("AED", exs[1].name)
        assertEquals(dateFromString("2020-09-03T19:00:00.000Z"), exs[1].date)
        assertEquals(null, exs[1].location)
        assertEquals("AED", exs[2].name)
        assertEquals(dateFromString("2020-09-17T19:00:00.000Z"), exs[2].date)
        assertEquals(null, exs[2].location)
        assertEquals("ALGA", exs[3].name)
        assertEquals(dateFromString("2020-07-22T14:00:00.000Z"), exs[3].date)
        assertEquals(null, exs[3].location)
        assertEquals("ALGA", exs[4].name)
        assertEquals(dateFromString("2020-09-12T10:00:00.000Z"), exs[4].date)
        assertEquals(null, exs[4].location)
        assertEquals("ALGA", exs[5].name)
        assertEquals(dateFromString("2020-09-24T19:00:00.000Z"), exs[5].date)
        assertEquals(null, exs[5].location)
    }

    @Test
    fun whenFileIsMismatched_thenThrowYamlExceptio() {
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased",
            "/generic/academic-calendar/academic-calendar.yml"
        )
        val f = ExamScheduleFactory()
        val ye = assertThrows<YAMLException> { f.parse(path) }
        assertEquals("Invalid yaml", ye.message)
    }
    @Test
    fun whenFileIncomplete_thenThrowYamlExceptio() {
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased",
            "/generic/exam-schedule/incomplete-exam-schedule.yml"
        )
        val f = ExamScheduleFactory()
        val ye = assertThrows<YAMLException> { f.parse(path) }
        assertEquals("Invalid yaml", ye.message)
    }
}