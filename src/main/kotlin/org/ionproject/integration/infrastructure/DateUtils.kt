package org.ionproject.integration.infrastructure

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    init {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    private const val CALENDAR_SIMPLE_FORMAT = "yyyy-MM-dd"
    private const val PT_DATA_RANGE_DELIMITERS_REGEX = "\\b(?:\\sa\\s|\\se\\s)\\b"
    private const val PT_DATE_DELIMITER = " de "
    private const val CALENDAR_ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss[X]"

    private val localePT = Locale
        .Builder()
        .setLanguageTag("pt")
        .setRegion("pt")
        .build()

    /**
     * Locale definition  follows IETF BCP 47 Language Tags
     * https://tools.ietf.org/rfc/bcp/bcp47.txt
     * The tags are maintained by the IANA Language Subtag Registry
     * https://www.iana.org/assignments/language-subtag-registry/language-subtag-registry
     *
     * In the future this object can be extended to support additional languages,
     * allowing to receive the language specification from documents
     */
    fun getDateFrom(date: String): LocalDate {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(localePT)

        val localDate = LocalDate.parse(date.lowercase(localePT), formatter)

        if (localDate.year < 1900)
            throw IllegalArgumentException("Invalid Year: ${localDate.year}")

        return localDate
    }

    /**
     * Converts a LocalDate to String as per the format defined for JSON and YAML files.
     */
    fun formatToCalendarDate(localDate: LocalDate): String =
        localDate.format(DateTimeFormatter.ofPattern(CALENDAR_SIMPLE_FORMAT, localePT))

    /**
     * Converts a LocalDate to String as per the format defined in ISO 8601.
     */
    fun formatToISO8601(zonedDateTime: ZonedDateTime): String =
        zonedDateTime.format(DateTimeFormatter.ofPattern(CALENDAR_ISO8601_FORMAT))

    fun formatToISO8601(localDateTime: LocalDateTime): String {
        val zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"))
        return formatToISO8601(zonedDateTime)
    }

    fun isDateRange(eventDateString: String): Boolean =
        eventDateString.contains(PT_DATA_RANGE_DELIMITERS_REGEX.toRegex())

    /**
     * Gets a Date Range from a String based on a Range Delimiter
     * If the delimiter doesn't exist then end date is equal to begin date
     */
    fun getDateRange(eventDateString: String): IntervalDate {
        val fromDate: LocalDate
        val toDate: LocalDate

        if (isDateRange(eventDateString)) {

            val list = eventDateString.lowercase().split(PT_DATA_RANGE_DELIMITERS_REGEX.toRegex())

            // End date
            toDate = getDateFrom(list[1])

            // Begin date
            fromDate = buildBeginDate(list[0], toDate.month, toDate.year)
        } else {
            fromDate = getDateFrom(eventDateString)
            toDate = fromDate
        }

        return IntervalDate(fromDate, toDate)
    }

    fun getEvaluationDateTimeFrom(
        year: String,
        dayMonth: String, // Format: 15 Jul.(Quinta)
        time: String,
        duration: String,
        timeZone: String
    ): IntervalDateTime {
        val timeFormat = DateTimeFormatter.ofPattern("HH'h'mm")
        val durationFormat = DateTimeFormatter.ofPattern("H'h'mm")
        val dateFormat = DateTimeFormatter.ofPattern("d MMM yyyy", localePT)

        val timeParsed = LocalTime.parse(time, timeFormat)
        val durationParsed = LocalTime.parse(duration, durationFormat)

        // Format: d MMM yyyy - Ex.: 15 Jul 2021
        val rawDate = "${dayMonth.split(".").first()} $year"
        val date = LocalDate.parse(rawDate, dateFormat)

        val startDateTime = convertDateToUTC(LocalDateTime.of(date, timeParsed), timeZone)
        val endDateTime = convertDateToUTC(LocalDateTime.of(date, addToStartTime(timeParsed, durationParsed)), timeZone)
        return IntervalDateTime(startDateTime, endDateTime)
    }

    fun convertDateToUTC(datetime: LocalDateTime, oldZone: String): ZonedDateTime {
        val oldZoneId = ZoneId.of(oldZone)
        val newZoneId = ZoneId.of("UTC")
        return ZonedDateTime.of(datetime.atZone(oldZoneId).withZoneSameInstant(newZoneId).toLocalDateTime(), newZoneId)
    }

    private fun addToStartTime(time: LocalTime, duration: LocalTime): LocalTime =
        time.plusHours(duration.hour.toLong()).plusMinutes(duration.minute.toLong())

    private fun buildBeginDate(string: String, month: Month, year: Int): LocalDate {
        return if (isMonthAndYearUnavailable(string))
            LocalDate.of(year, month, string.toInt())
        else if (isYearUnavailable(string))
            getDateFrom(string + PT_DATE_DELIMITER + year)
        else
            getDateFrom(string)
    }

    private fun isYearUnavailable(string: String): Boolean = string.trim().takeLast(4).toIntOrNull() == null
    private fun isMonthAndYearUnavailable(string: String): Boolean = string.length <= 2
}

data class IntervalDateTime(
    val from: ZonedDateTime,
    val to: ZonedDateTime
)

data class IntervalDate(
    val from: LocalDate,
    val to: LocalDate
)
