package org.ionproject.integration.ui.input

import io.swagger.v3.oas.annotations.media.Schema
import org.ionproject.integration.application.job.CALENDAR_IDENTIFIER
import org.ionproject.integration.application.job.EVALUATIONS_IDENTIFIER
import org.ionproject.integration.application.job.JobType
import org.ionproject.integration.application.job.TIMETABLE_IDENTIFIER
import org.ionproject.integration.infrastructure.file.OutputFormat

data class CreateJobDto(
    @Schema(description = "Institution unique identifier.", example = "pt.ipl.isel")
    val institution: String? = null,

    @Schema(description = "Programme acronym. Only required for programme-specific jobs.", example = "LEIC")
    val programme: String? = null,

    @Schema(description = "Output format of the produced data.", implementation = OutputFormat::class)
    val format: String? = null,

    @Schema(
        description = "The desired job type.",
        allowableValues = [EVALUATIONS_IDENTIFIER, TIMETABLE_IDENTIFIER, CALENDAR_IDENTIFIER],
        example = TIMETABLE_IDENTIFIER
    )
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
