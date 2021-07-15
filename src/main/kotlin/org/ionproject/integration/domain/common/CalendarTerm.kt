package org.ionproject.integration.domain.common

import java.time.Year

data class CalendarTerm(
    val startYear: Year,
    val term: Term
) {
    override fun toString() = "${startYear.value}-${startYear.value + 1}-${term.number}"
}
