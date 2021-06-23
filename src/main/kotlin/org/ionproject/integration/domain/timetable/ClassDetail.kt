package org.ionproject.integration.domain.timetable

import java.lang.Integer.max

data class ClassDetail(
    val acronym: String,
    val location: String,
    val type: EventCategory
) {
    companion object {
        fun from(text: String): ClassDetail {
            val firstIndex = text.indexOf('(')

            val acronym = getAcronym(text)
            val location = getLocation(text)
            val type = when (getClassType(text, firstIndex)) {
                "L" -> EventCategory.LAB
                "T" -> EventCategory.LECTURE
                "T/P" -> EventCategory.LECTURE_PRACTICE
                "P" -> EventCategory.PRACTICE
                else -> throw IllegalArgumentException("Unknown Event Category")
            }

            return ClassDetail(acronym, location, type)
        }

        private fun getClassType(text: String, firstIndex: Int): String =
            text.substring(firstIndex + 1, max(text.indexOf(')', firstIndex), 0)).trim()

        private fun getLocation(text: String): String = text.substring(text.lastIndexOf(')') + 1).trim()

        private fun getAcronym(text: String): String = text.substringBefore('[').trim()
    }
}
