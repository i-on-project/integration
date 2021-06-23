package org.ionproject.integration.domain.timetable

enum class Weekday(val shortName: String) {
    MONDAY("MO"),
    TUESDAY("TU"),
    WEDNESDAY("WE"),
    THURSDAY("TH"),
    FRIDAY("FR"),
    SATURDAY("SA"),
    SUNDAY("SU");

    companion object Factory {
        private val options = setOf(RegexOption.IGNORE_CASE)

        private val portugueseMappings = mapOf(
            "segunda[- ]?(feira)?".toRegex(options) to MONDAY,
            "ter[cç]a[- ]?(feira)?".toRegex(options) to TUESDAY,
            "quarta[- ]?(feira)?".toRegex(options) to WEDNESDAY,
            "quinta[- ]?(feira)?".toRegex(options) to THURSDAY,
            "sexta[- ]?(feira)?".toRegex(options) to FRIDAY,
            "s[áa]bado".toRegex(options) to SATURDAY,
            "domingo".toRegex(options) to SUNDAY
        )

        fun fromPortuguese(text: String): Weekday {
            val weekday = portugueseMappings
                .filter { it.key.matches(text) }
                .map { it.value }
                .firstOrNull()

            return weekday ?: throw IllegalArgumentException("Invalid weekday: $text")
        }
    }
}
