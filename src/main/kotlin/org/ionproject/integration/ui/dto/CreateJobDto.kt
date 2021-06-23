package org.ionproject.integration.ui.dto

import org.ionproject.integration.application.job.JobType
import java.lang.UnsupportedOperationException

data class CreateJobDto(
    val institution: String? = null,
    val programme: String? = null,
    val format: String? = null,
    val type: String? = null
) {
    fun toSafeDto(type: JobType): SafeJobDto {
        return when (type) {
            JobType.TIMETABLE -> SafeTimetableJobDto(institution!!, programme!!, format!!)
            JobType.ACADEMIC_CALENDAR -> SafeCalendarJobDto(institution!!, format!!)
            JobType.EXAM_SCHEDULE -> throw UnsupportedOperationException("Exam Schedule not yet supported")
        }
    }
}