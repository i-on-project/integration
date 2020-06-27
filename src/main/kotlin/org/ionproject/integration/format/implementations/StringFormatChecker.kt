package org.ionproject.integration.format.implementations

import org.ionproject.integration.format.interfaces.IFormatChecker

class StringFormatChecker(pattern: String) : IFormatChecker {

    private val regex = Regex(pattern)

    override fun checkFormat(content: String): Boolean {
        return regex.containsMatchIn(content)
    }
}
