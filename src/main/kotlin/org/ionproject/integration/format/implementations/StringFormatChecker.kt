package org.ionproject.integration.format.implementations

import org.ionproject.integration.format.interfaces.FormatChecker

class StringFormatChecker(pattern: String) : FormatChecker {

    private val regex = Regex(pattern)

    override fun checkFormat(content: String): Boolean {
        return regex.containsMatchIn(content)
    }
}
