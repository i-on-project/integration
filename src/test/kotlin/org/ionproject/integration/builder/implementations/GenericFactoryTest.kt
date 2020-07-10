package org.ionproject.integration.builder.implementations

import org.ionproject.integration.model.internal.generic.JobType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GenericFactoryTest {
    @Test
    fun whenJobTypeIsTimetable_thenGetTimetableFactory() {
        val f = GenericFactory.getFactory(JobType.TIMETABLE)
        assertEquals("TimetableFactory", f::class.java.simpleName)
    }
    @Test
    fun whenJobTypeIsAcademicCalendar_thenGetAcademicCalendarFactory() {
        val f = GenericFactory.getFactory(JobType.ACADEMIC_CALENDAR)
        assertEquals("AcademicCalendarFactory", f::class.java.simpleName)
    }
    @Test
    fun whenJobTypeIsExamSchedule_thenGetExamScheduleFactory() {
        val f = GenericFactory.getFactory(JobType.EXAM_SCHEDULE)
        assertEquals("ExamCalendarFactory", f::class.java.simpleName)
    }
}
