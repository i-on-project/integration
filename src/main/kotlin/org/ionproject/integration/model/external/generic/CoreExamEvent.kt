package org.ionproject.integration.model.external.generic

import java.time.ZoneId
import org.ionproject.integration.model.external.generic.exceptions.ExamScheduleException
import org.ionproject.integration.model.internal.generic.Exam

data class CoreExamEvent(
    val title: String = "",
    val description: String,
    val location: Array<String>?,
    val startDate: String,
    val endDate: String,
    val category: Int = 1
) {
    companion object {
        fun fromInternalModelExams(exams: List<Exam>): List<CoreExamEvent> {
            return exams.map { e -> mapExamToExamEvent(e) }
        }

        private fun mapExamToExamEvent(e: Exam): CoreExamEvent {
            val locations = if (e.location != null) arrayOf(e.location) else null
            val startDate = e.startDate
            val endDate = e.endDate
            if (startDate.after(endDate)) {
                throw ExamScheduleException("Start date $startDate is after end date $endDate")
            }
            val beginDateTime =
                startDate
                    .toInstant()
                    .atZone(ZoneId.of("GMT"))
                    .toLocalDateTime()
            val endDateTime =
                endDate
                    .toInstant()
                    .atZone(ZoneId.of("GMT"))
                    .toLocalDateTime()
            return CoreExamEvent(
                "Exame de ${e.name}",
                e.name,
                locations,
                beginDateTime.toString(),
                endDateTime.toString()
            )
        }
    }
}
