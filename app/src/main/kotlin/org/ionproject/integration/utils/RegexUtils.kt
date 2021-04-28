package org.ionproject.integration.utils

object RegexUtils {
    fun isMatch(pattern: String, input: String): Boolean {
        return Regex(pattern).containsMatchIn(input)
    }

    fun findMatches(pattern: String, input: String, regexOption: RegexOption = RegexOption.IGNORE_CASE): List<String> {
        return Regex(pattern, regexOption)
            .findAll(input)
            .map { it.groupValues }
            .flatten()
            .toList()
    }
}
