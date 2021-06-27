package org.ionproject.integration.application.job

enum class JobType(val identifier: String) {
    TIMETABLE("timetable"),
    EXAM_SCHEDULE("evaluations"),
    ACADEMIC_CALENDAR("calendar");

    companion object Factory {
        fun of(name: String?): JobType? = values().find { it.identifier.equals(name?.trim(), ignoreCase = true) }
    }
}
