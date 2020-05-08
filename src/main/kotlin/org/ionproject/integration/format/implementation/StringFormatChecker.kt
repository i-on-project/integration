package org.ionproject.integration.format.implementation

import org.ionproject.integration.format.`interface`.FormatChecker

class StringFormatChecker(pattern: String) : FormatChecker {

    private val regex = Regex(pattern)

    override fun checkFormat(content: String): Boolean {
        return regex.containsMatchIn(content)
    }
}
