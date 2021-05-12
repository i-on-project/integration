package org.ionproject.integration.utils

import org.ionproject.integration.model.external.timetable.Language

object IgnoredWords {
    private val portugueseWords = listOf("de", "do", "da", "e", "o", "a", "para", "dos", "das")
    private val englishWords = listOf("from", "to", "by", "as", "of")

    fun of(language: Language): List<String> =
        when (language) {
            Language.PT -> portugueseWords
            Language.EN_GB, Language.EN_US -> englishWords
        }
}

fun generateAcronym(text: String, ignoredWords: List<String> = emptyList()): String =
    text.split("""[\s_\-,]""".toRegex())
        .filter(String::isNotEmpty)
        .filterNot(ignoredWords::contains)
        .map(String::first)
        .joinToString("")
        .uppercase()
