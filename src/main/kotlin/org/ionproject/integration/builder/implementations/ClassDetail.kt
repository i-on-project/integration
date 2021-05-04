package org.ionproject.integration.builder.implementations

import java.lang.Integer.max

data class ClassDetail(
    val acronym: String,
    val location: String,
    val type: String
) {
    companion object {
        fun from(text: String): ClassDetail {
            val firstIndex = text.indexOf('(')

            val acronym = text.substringBefore('[').trim()
            val location = text.substring(text.lastIndexOf(')') + 1).trim()
            val type = text.substring(firstIndex + 1, max(text.indexOf(')', firstIndex), 0)).trim()

            return ClassDetail(acronym, location, type)
        }
    }
}
