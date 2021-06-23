package org.ionproject.integration.infrastructure

interface IFormatChecker {
    fun checkFormat(content: String): Boolean
}
