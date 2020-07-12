package org.ionproject.integration.model.internal.generic

import java.time.Instant
import java.time.ZoneId
import java.util.Date
import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.external.generic.exceptions.ExamScheduleException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ExamScheduleTest {

    private fun convertDate(date: Date): String {
        return date
            .toInstant()
            .atZone(ZoneId.of("GMT"))
            .toLocalDateTime()
            .toString()
    }

    private val schoolAName = "School A"
    private val schoolAAcr = "SA"
    private val school = School(schoolAName, schoolAAcr)
    private val programmeName = "Programme A"
    private val programmeAcr = "PA"
    private val programme = Programme(programmeName, programmeAcr)
    private val academicYear = "2019/2020"
    private val exam1Name = "exam1"
    private val beginInstant = Instant.now()
    private val exam1StartDate = Date.from(beginInstant)
    private val exam1EndDate = Date.from(beginInstant.plusSeconds(3600))
    private val exam1StartDateAfter = Date.from(beginInstant.plusSeconds(6000))
    private val exam1Location = "E1.11"
    private val exams = listOf(Exam(exam1Name, exam1StartDate, exam1EndDate, exam1Location))
    private val examsInvalid = listOf(Exam(exam1Name, exam1StartDateAfter, exam1EndDate, exam1Location))

    @Test
    fun whenExamScheduleIsSuccessfullyConverted_thenCoreExamScheduleIsFilled() {
        val es = ExamSchedule(school, programme, academicYear, exams)

        val ces = es.toCore() as CoreExamSchedule

        assertions(ces)
    }

    private fun assertions(ces: CoreExamSchedule) {
        assertEquals("pt-PT", ces.language)
        assertEquals(schoolAName, ces.school.name)
        assertEquals(schoolAAcr, ces.school.acr)
        assertEquals(programmeName, ces.programme.name)
        assertEquals(programmeAcr, ces.programme.acr)
        assertEquals(academicYear, ces.academicYear)
        assertEquals(exam1Name, ces.exams[0].label.acr)
        assertNull(ces.exams[0].label.name)
        assertEquals(convertDate(exam1StartDate), ces.exams[0].events[0].startDate)
        assertEquals(convertDate(exam1EndDate), ces.exams[0].events[0].endDate)
        assertEquals(1, ces.exams[0].events[0].location?.size)
        assertEquals(exam1Location, ces.exams[0].events[0].location?.get(0))
        assertEquals(1, ces.exams[0].events[0].category)
        assertEquals(exam1Name, ces.exams[0].events[0].description)
        assertEquals("Exame de $exam1Name", ces.exams[0].events[0].title)
    }

    @Test
    fun whenStartDateIsAfterEndDate_thenThrowExamScheduleException() {
        val es = ExamSchedule(school, programme, academicYear, examsInvalid)

        val ces = assertThrows<ExamScheduleException> { es.toCore() }

        val msg = ces.message
        Assertions.assertTrue(msg!!.contains("Start date") && msg.contains("is after end date"))
    }
}
