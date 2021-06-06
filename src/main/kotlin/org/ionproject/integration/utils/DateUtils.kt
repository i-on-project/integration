package org.ionproject.integration.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val CALENDAR_SIMPLE_FORMAT = "yyyy-MM-dd"

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
}
