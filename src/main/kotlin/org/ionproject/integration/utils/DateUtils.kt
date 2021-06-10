package org.ionproject.integration.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
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
    private const val CALENDAR_ISO8601_FORMAT = "yyyyMMdd'T'HHmmssX"

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
    fun formatToISO8601(localDate: LocalDate): String =
        SimpleDateFormat(CALENDAR_ISO8601_FORMAT).format(localDate)

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

    private fun buildBeginDate(string: String, month: Month, year: Int): LocalDate {
        if (isMonthAndYearUnavailable(string))
            return LocalDate.of(year, month, string.toInt())
        else if (isYearUnavailable(string))
            return getDateFrom(string + PT_DATE_DELIMITER + year)
        else
            return getDateFrom(string)
    }

    private fun isYearUnavailable(string: String): Boolean = string.trim().takeLast(4).toIntOrNull() == null
    private fun isMonthAndYearUnavailable(string: String): Boolean = string.length <= 2
}

data class IntervalDate(
    val from: LocalDate,
    val to: LocalDate
)
