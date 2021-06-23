package org.ionproject.integration.infrastructure

interface IBytesFormatChecker {
    fun isValidFormat(bytes: ByteArray): Boolean
}
