package org.ionproject.integration.utils

import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val CALENDAR_SIMPLE_FORMAT = "yyyy-MM-dd"
    private const val PT_DATA_RANGE_DELIMITERS_REGEX = "\\b(?:\\sa\\s|\\se\\s)\\b"
    private const val PT_DATE_DELIMITER = " de "
    private val ptDataRangeDelimiters =
        listOf(" a ", " e ")

    /**
     * Locale definition  follows IETF BCP 47 Language Tags
     * https://tools.ietf.org/rfc/bcp/bcp47.txt
     * The tags are maintained by the IANA Language Subtag Registry
     * https://www.iana.org/assignments/language-subtag-registry/language-subtag-registry
     *
     * In the future this object can be extended to support additional languages,
     * allowing to receive the language specification from documents
     */
    fun getDateFrom(date: String): Date {
        val locale = Locale
            .Builder()
            .setLanguageTag("pt")
            .setRegion("pt")
            .build()
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale)

        val localDate = LocalDate.parse(date.lowercase(locale), formatter)

        if (localDate.year < 1900)
            throw IllegalArgumentException("Invalid Year: ${localDate.year}")

        return Date.from(localDate.atStartOfDay().toInstant(ZoneOffset.UTC))
    }

    /**
     * Converts a Date to String as per the format defined for JSON and YAML files.
     */
    fun getDateRepresentation(date: Date): String =
        SimpleDateFormat(CALENDAR_SIMPLE_FORMAT).format(date)

    fun isDateRange(eventDateString: String): Boolean =
        eventDateString.contains(PT_DATA_RANGE_DELIMITERS_REGEX.toRegex())

    /**
     * Gets a Date Range from a String based on a Range Delimiter
     * If the delimiter doesn't exist then end date is equal to begin date
     */
    fun getDateRange(eventDateString: String): List<Date> {
        val dates = mutableListOf<Date>()

        if (isDateRange(eventDateString)) {

            val list = eventDateString.lowercase().split(PT_DATA_RANGE_DELIMITERS_REGEX.toRegex())

            val endDate = getDateFrom(list[1])
            val calendar = Calendar.getInstance()
            calendar.time = endDate
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            // Begin date
            dates.add(buildBeginDate(list[0], month, year))

            // End date
            dates.add(endDate)
        } else {
            val singleDate = getDateFrom(eventDateString)
            dates.add(singleDate)
            dates.add(singleDate)
        }

        return dates.toList()
    }

    private fun buildBeginDate(string: String, month: Int, year: Int): Date {
        if (string.length <= 2)
            return getDateFrom(string + PT_DATE_DELIMITER + getMonth(month) + PT_DATE_DELIMITER + year)
        else if (string.trim().takeLast(4).toIntOrNull() == null)
            return getDateFrom(string + PT_DATE_DELIMITER + year)
        else
            return getDateFrom(string)
    }

    private fun getMonth(month: Int): String {
        val locale = Locale
            .Builder()
            .setLanguageTag("pt")
            .setRegion("pt")
            .build()
        return DateFormatSymbols(locale).months[month]
    }
}
