package org.ionproject.integration.builder.implementations

import org.ionproject.integration.model.external.timetable.EventCategory
import org.ionproject.integration.utils.Try
import java.lang.Integer.max

data class ClassDetail(
    val acronym: String,
    val location: String,
    val type: EventCategory
) {
    companion object {
        fun from(text: String): ClassDetail {
            val firstIndex = text.indexOf('(')

            val acronym = text.substringBefore('[').trim()
            val location = text.substring(text.lastIndexOf(')') + 1).trim()
            val type = when (text.substring(firstIndex + 1, max(text.indexOf(')', firstIndex), 0)).trim()) {
                "L" -> EventCategory.LAB
                "T" -> EventCategory.LECTURE
                "T/P" -> EventCategory.LECTURE_PRACTICE
                "P" -> EventCategory.PRACTICE
                else -> throw IllegalArgumentException("Unknown Event Category")
            }

            return ClassDetail(acronym, location, type)
        }
    }
}
