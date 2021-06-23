package org.ionproject.integration.infrastructure.text

class StringFormatChecker(pattern: String) : IFormatChecker {

    private val regex = Regex(pattern)

    override fun checkFormat(content: String): Boolean {
        return regex.containsMatchIn(content)
    }
}
