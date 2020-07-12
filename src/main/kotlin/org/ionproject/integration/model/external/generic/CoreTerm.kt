package org.ionproject.integration.model.external.generic

import java.time.ZoneId
import java.util.Date
import org.ionproject.integration.model.external.generic.exceptions.AcademicCalendarException
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.internal.generic.Evaluation
import org.ionproject.integration.model.internal.generic.Event
import org.ionproject.integration.model.internal.generic.Interruption
import org.ionproject.integration.model.internal.generic.OtherEvent
import org.ionproject.integration.model.internal.generic.Term
import org.ionproject.integration.model.internal.generic.TermDetail

data class CoreTerm(
    val school: School,
    val startDate: String,
    val endDate: String,
    val language: String,
    val calendarTerm: String,
    val intervals: List<CoreInterval>
) {

    companion object {
        var startDate: Date = Date(Long.MAX_VALUE)
        var endDate: Date = Date(Long.MIN_VALUE)

        fun fromInternalTerm(t: Term, school: School): CoreTerm {
            startDate = Date(Long.MAX_VALUE)
            endDate = Date(Long.MIN_VALUE)
            val language = "pt-PT"
            val calendarTerm = t.calendarTerm
            val intervals = mutableListOf<CoreInterval>()

            intervals.addAll(interruptionToInterval(t.interruptions))
            intervals.addAll(evaluationsToInterval(t.evaluations))
            intervals.addAll(detailsToInterval(t.details))
            intervals.addAll(otherEventsToInterval(t.otherEvents))

            validateDates(startDate, endDate)
            val startDateString = convertDateToLocalDateString(startDate)
            val endDateString = convertDateToLocalDateString(endDate)
            return CoreTerm(school, startDateString, endDateString, language, calendarTerm, intervals)
        }

        private fun validateDates(startDate: Date, endDate: Date) {
            if (startDate.after(endDate)) {
                throw AcademicCalendarException(
                    "Start date $startDate is after end date $endDate"
                )
            }
        }

        private fun interruptionToInterval(
            interruptions: List<Interruption>
        ): List<CoreInterval> {
            return interruptions.map { interruption ->

                checkForMinAndMax(interruption)

                validateDates(interruption.startDate, interruption.endDate
                )
                CoreInterval(
                    convertDateToLocalDateString(interruption.startDate),
                    convertDateToLocalDateString(interruption.endDate),
                    interruption.name,
                    null,
                    null,
                    listOf(2)
                )
            }
        }

        private fun checkForMinAndMax(event: Event) {
            if (event.startDate.before(startDate)) {
                startDate = event.startDate
            }
            if (event.endDate.after(endDate)) {
                endDate = event.endDate
            }
        }

        private fun evaluationsToInterval(
            evaluations: List<Evaluation>
        ): List<CoreInterval> {
            return evaluations.map { evaluation ->

                checkForMinAndMax(evaluation)

                validateDates(evaluation.startDate, evaluation.endDate)
                CoreInterval(
                    convertDateToLocalDateString(evaluation.startDate),
                    convertDateToLocalDateString(evaluation.endDate),
                    evaluation.name,
                    null,
                    if (evaluation.duringLectures) listOf(1, 2) else listOf(1),
                    if (!evaluation.duringLectures) listOf(2) else null
                )
            }
        }

        private fun detailsToInterval(details: List<TermDetail>): List<CoreInterval> {
            return details.map { detail ->

                checkForMinAndMax(detail)

                validateDates(detail.startDate, detail.endDate)

                CoreInterval(
                    convertDateToLocalDateString(detail.startDate),
                    convertDateToLocalDateString(detail.endDate),
                    detail.name,
                    detail.curricularTerm,
                    listOf(2),
                    null
                )
            }
        }

        private fun otherEventsToInterval(
            others: List<OtherEvent>
        ): List<CoreInterval> {
            return others.map { other ->

                checkForMinAndMax(other)

                validateDates(other.startDate, other.endDate)

                CoreInterval(
                    convertDateToLocalDateString(other.startDate),
                    convertDateToLocalDateString(other.endDate),
                    other.name,
                    null,
                    null,
                    null
                )
            }
        }

        private fun convertDateToLocalDateString(date: Date): String {
            return date
                .toInstant()
                .atZone(ZoneId.of("GMT"))
                .toLocalDate()
                .toString()
        }
    }
}
