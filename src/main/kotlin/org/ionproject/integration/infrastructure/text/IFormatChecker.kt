package org.ionproject.integration.infrastructure.text

interface IFormatChecker {
    fun checkFormat(content: String): Boolean
}
