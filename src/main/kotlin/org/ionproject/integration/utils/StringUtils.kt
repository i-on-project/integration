package org.ionproject.integration.utils

import org.ionproject.integration.model.external.timetable.Language
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object DateFormat {
    init {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    private val format = "yyyyMMdd'T'HHmmssX"
    private val formatter = SimpleDateFormat(format)

    fun format(date: Date): String = formatter.format(date)
}

object IgnoredWords {
    private val portugueseWords =
        listOf("de", "do", "da", "e", "o", "a", "para", "dos", "das", "dr", "dr.", "dra", "dra.", "em")
    private val englishWords = listOf("from", "to", "by", "as", "of")

    fun of(language: Language): List<String> =
        when (language) {
            Language.PT -> portugueseWords
            Language.EN_GB, Language.EN_US -> englishWords
        }
}

fun List<String>.containsCaseInsensitive(string: String): Boolean = any { it.equals(string, ignoreCase = true) }

fun generateAcronym(text: String, ignoredWords: List<String> = emptyList()): String =
    text.split("""[\s_\-,]""".toRegex())
        .filter(String::isNotEmpty)
        .filterNot(ignoredWords::containsCaseInsensitive)
        .map(String::first)
        .joinToString("")
        .uppercase()
