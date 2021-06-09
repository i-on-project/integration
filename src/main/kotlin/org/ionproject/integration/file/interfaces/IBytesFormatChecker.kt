package org.ionproject.integration.file.interfaces

interface IBytesFormatChecker {
    fun isValidFormat(bytes: ByteArray): Boolean
}
