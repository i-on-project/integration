package org.ionproject.integration.domain.common

import java.time.Year

data class CalendarTerm(
    val startYear: Year,
    val endYear: Year,
    val term: Term
) {
    override fun toString() = "${startYear.value}-${endYear.value}-${term.number}"
}
