package org.ionproject.integration.infrastructure.text

import org.ionproject.integration.infrastructure.text.IFormatChecker

class StringFormatChecker(pattern: String) : IFormatChecker {

    private val regex = Regex(pattern)

    override fun checkFormat(content: String): Boolean {
        return regex.containsMatchIn(content)
    }
}
