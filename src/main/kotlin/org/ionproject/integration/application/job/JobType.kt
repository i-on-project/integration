package org.ionproject.integration.application.job

const val TIMETABLE_IDENTIFIER = "timetable"
const val CALENDAR_IDENTIFIER = "calendar"
const val EVALUATIONS_IDENTIFIER = "evaluations"

enum class JobType(val identifier: String) {
    TIMETABLE(TIMETABLE_IDENTIFIER),
    ACADEMIC_CALENDAR(CALENDAR_IDENTIFIER),
    EXAM_SCHEDULE(EVALUATIONS_IDENTIFIER);

    companion object Factory {
        fun of(name: String?): JobType? = values().find { it.identifier.equals(name?.trim(), ignoreCase = true) }
    }
}
