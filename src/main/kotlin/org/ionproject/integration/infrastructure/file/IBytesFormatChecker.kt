package org.ionproject.integration.infrastructure.file

interface IBytesFormatChecker {
    fun isValidFormat(bytes: ByteArray): Boolean
}
