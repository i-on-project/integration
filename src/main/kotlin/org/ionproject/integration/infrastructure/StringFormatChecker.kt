package org.ionproject.integration.infrastructure

import org.ionproject.integration.infrastructure.IFormatChecker

class StringFormatChecker(pattern: String) : IFormatChecker {

    private val regex = Regex(pattern)

    override fun checkFormat(content: String): Boolean {
        return regex.containsMatchIn(content)
    }
}
