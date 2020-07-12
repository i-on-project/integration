package org.ionproject.integration.model.internal.generic

import java.time.Instant
import java.time.ZoneId
import java.util.Date
import org.ionproject.integration.model.external.generic.CoreAcademicCalendar
import org.ionproject.integration.model.external.generic.exceptions.AcademicCalendarException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AcademicCalendarTest {

    private fun convertDate(date: Date): String {
        return date
            .toInstant()
            .atZone(ZoneId.of("GMT"))
            .toLocalDate()
            .toString()
    }

    val schoolAName = "School A"
    val schoolAAcr = "SA"
    val school = School(schoolAName, schoolAAcr)
    val interruptionName = "interruption1"
    val evaluationName = "evaluation1"
    val evaluationName2 = "evaluation2"
    val termDetailName = "detail1"
    val otherEventName = "other1"
    val beginInstant = Instant.now()
    val interruptionBeginDate = Date.from(beginInstant)
    val interruptionEndDate = Date.from(beginInstant.plusSeconds(3600))
    val evaluationBeginDate = Date.from(beginInstant.plusSeconds(600))
    val evaluationEndDate = Date.from(beginInstant.plusSeconds(800))
    val detailStartDate = Date.from(beginInstant)
    val detailEndDate = Date.from(beginInstant.plusSeconds(7200))
    val curricularTerms = listOf(
        CurricularTerm(1),
        CurricularTerm(2),
        CurricularTerm(3)
    )
    val otherEventStartDate = Date.from(beginInstant)
    val otherEventEndDate = Date.from(beginInstant.plusSeconds(7200))
    val interruptions = listOf(Interruption(interruptionName, interruptionBeginDate, interruptionEndDate))
    val evaluations = listOf(Evaluation(evaluationName, evaluationBeginDate, evaluationEndDate, false),
        Evaluation(evaluationName2, evaluationBeginDate, evaluationEndDate, true))
    val details = listOf(TermDetail(termDetailName, curricularTerms, detailStartDate, detailEndDate))
    val otherEvents = listOf(OtherEvent(otherEventName, otherEventStartDate, otherEventEndDate))

    val invalidDate = Date.from(beginInstant.plusSeconds(600000))
    val endDateBefore = Date.from(beginInstant)
    val invalidInterruptions = listOf(Interruption(interruptionName, invalidDate, endDateBefore))
    val invalidEvaluations = listOf(Evaluation(evaluationName, invalidDate, endDateBefore, false))
    val invalidDetails = listOf(TermDetail(termDetailName, curricularTerms, invalidDate, endDateBefore))
    val invalidOthers = listOf(OtherEvent(otherEventName, invalidDate, endDateBefore))

    @Test
    fun whenConversionToCoreIsSuccessfull_thenCoreModelIsFilled() {
        val term = Term("1920i", interruptions, evaluations, details, otherEvents)
        val ac = AcademicCalendar(school, listOf(term))

        val cac = ac.toCore() as CoreAcademicCalendar

        assertions(cac)
    }

    @Test
    fun whenBeginDateIsAfterEndDateInInterruptions_thenThrowException() {
        val invalidTerm = Term("1920i", invalidInterruptions, evaluations, details, otherEvents)
        val acInvalid = AcademicCalendar(school, listOf(invalidTerm))

        val ace =
            assertThrows<AcademicCalendarException> { acInvalid.toCore() }

        assertExceptionMessage(ace)
    }

    @Test
    fun whenBeginDateIsAfterEndDateInEvaluations_thenThrowAcademicCalendarException() {
        val invalidTerm = Term("1920i", interruptions, invalidEvaluations, details, otherEvents)
        val acInvalid = AcademicCalendar(school, listOf(invalidTerm))

        val ace =
            assertThrows<AcademicCalendarException> { acInvalid.toCore() }

        assertExceptionMessage(ace)
    }

    @Test
    fun whenBeginDateIsAfterEndDateInDetails_thenThrowAcademicCalendarException() {
        val invalidTerm = Term("1920i", interruptions, evaluations, invalidDetails, otherEvents)
        val acInvalid = AcademicCalendar(school, listOf(invalidTerm))

        val ace =
            assertThrows<AcademicCalendarException> { acInvalid.toCore() }

        assertExceptionMessage(ace)
    }
    @Test
    fun whenBeginDateIsAfterEndDateInOtherEvents_thenThrowAcademicCalendarException() {
        val invalidTerm = Term("1920i", interruptions, evaluations, details, invalidOthers)
        val acInvalid = AcademicCalendar(school, listOf(invalidTerm))

        val ace =
            assertThrows<AcademicCalendarException> { acInvalid.toCore() }

        assertExceptionMessage(ace)
    }

    private fun assertExceptionMessage(ace: AcademicCalendarException) {
        val msg = ace.message
        assertTrue(msg!!.contains("Start date") && msg.contains("is after end date"))
    }

    private fun assertions(cac: CoreAcademicCalendar) {
        assertEquals(schoolAName, cac.terms[0].school.name)
        assertEquals(schoolAAcr, cac.terms[0].school.acr)

        assertEquals(1, cac.terms.size)
        assertEquals(interruptionName, cac.terms[0].intervals[0].name)
        assertEquals(convertDate(interruptionBeginDate), cac.terms[0].intervals[0].startDate)
        assertEquals(convertDate(interruptionEndDate), cac.terms[0].intervals[0].endDate)
        assertNull(cac.terms[0].intervals[0].curricularTerm)
        assertEquals(1, cac.terms[0].intervals[0].excludes?.size)
        assertEquals(2, cac.terms[0].intervals[0].excludes?.get(0))
        assertNull(cac.terms[0].intervals[0].types)

        assertEquals(evaluationName, cac.terms[0].intervals[1].name)
        assertEquals(convertDate(evaluationBeginDate), cac.terms[0].intervals[1].startDate)
        assertEquals(convertDate(evaluationEndDate), cac.terms[0].intervals[1].endDate)
        assertEquals(1, cac.terms[0].intervals[1].excludes?.size)
        assertEquals(2, cac.terms[0].intervals[1].excludes?.get(0))

        assertEquals(evaluationName2, cac.terms[0].intervals[2].name)
        assertEquals(convertDate(evaluationBeginDate), cac.terms[0].intervals[2].startDate)
        assertEquals(convertDate(evaluationEndDate), cac.terms[0].intervals[2].endDate)
        assertNull(cac.terms[0].intervals[2].excludes)
        assertTrue(arrayOf(1, 2).contentEquals(cac.terms[0].intervals[2].types?.toTypedArray()!!))

        assertEquals(termDetailName, cac.terms[0].intervals[3].name)
        assertEquals(convertDate(detailStartDate), cac.terms[0].intervals[3].startDate)
        assertEquals(convertDate(detailEndDate), cac.terms[0].intervals[3].endDate)
        assertEquals(curricularTerms, cac.terms[0].intervals[3].curricularTerm)
        assertEquals(2, cac.terms[0].intervals[3].types?.get(0))
        assertNull(cac.terms[0].intervals[3].excludes)

        assertEquals(otherEventName, cac.terms[0].intervals[4].name)
        assertEquals(convertDate(otherEventStartDate), cac.terms[0].intervals[4].startDate)
        assertEquals(convertDate(otherEventEndDate), cac.terms[0].intervals[4].endDate)
        assertNull(cac.terms[0].intervals[4].excludes)
        assertNull(cac.terms[0].intervals[4].types)
        assertNull(cac.terms[0].intervals[4].curricularTerm)
    }
}
