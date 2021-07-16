package org.ionproject.integration.application.dto

data class CalendarTermDto(
    val startYear: Int,
    val term: Int
) {
    override fun toString(): String = "$startYear-${startYear + 1}-$term"
}
