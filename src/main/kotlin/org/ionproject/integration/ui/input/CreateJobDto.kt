package org.ionproject.integration.ui.input

import org.ionproject.integration.application.job.JobType

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
            JobType.EXAM_SCHEDULE -> SafeEvaluationsJobDto(institution!!, programme!!, format!!)
        }
    }
}
