package org.ionproject.integration.builder.implementations

import org.ionproject.integration.builder.interfaces.IGenericFactory
import org.ionproject.integration.model.internal.generic.JobType

object GenericFactory {
    fun getFactory(jobType: JobType): IGenericFactory {
        return when (jobType) {
            JobType.TIMETABLE -> TimetableFactory()
            JobType.ACADEMIC_CALENDAR -> AcademicCalendarFactory()
            JobType.EXAM_SCHEDULE -> ExamCalendarFactory()
        }
    }
}
