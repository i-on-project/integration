package org.ionproject.integration.application.dto

import org.ionproject.integration.domain.Term

data class CalendarTerm(
    val startYear: Int,
    val term: Term
) {
    override fun toString(): String = "$startYear-${startYear + 1}-${term.number}"
}