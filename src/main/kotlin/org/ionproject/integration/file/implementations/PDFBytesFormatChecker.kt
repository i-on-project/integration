package org.ionproject.integration.file.implementations

import org.ionproject.integration.file.interfaces.IBytesFormatChecker
import org.springframework.stereotype.Service

@Service
class PDFBytesFormatChecker : IBytesFormatChecker {
    private val PDF_HEADER = "%PDF-1."
    private val MINIMUM_PDF_VERSION = '0'
    private val MAXIMUM_PDF_VERSION = '7'
    private val HEADER_MIN_VERSION_POSITION = 7
    private val HEADER_RANGE = 0..6
    private val HEADER_LENGTH = 7

    override fun isValidFormat(bytes: ByteArray): Boolean {
        if (bytes.size < HEADER_LENGTH)
            return false
        val minorVersion: Char = bytes[HEADER_MIN_VERSION_POSITION].toInt().toChar()
        val headerBytes: ByteArray = bytes.slice(HEADER_RANGE).toByteArray()
        val header = String(headerBytes, Charsets.UTF_8)

        return header == PDF_HEADER && minorVersion in MINIMUM_PDF_VERSION..MAXIMUM_PDF_VERSION
    }
}
