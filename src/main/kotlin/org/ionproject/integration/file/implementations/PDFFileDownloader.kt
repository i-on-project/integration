package org.ionproject.integration.file.implementations

import org.ionproject.integration.file.exceptions.InvalidFormatException

class PDFFileDownloader : AbstractFileDownloader() {
    private val PDF_HEADER = "%PDF-1."
    private val MINIMUM_PDF_VERSION = '0'
    private val MAXIMUM_PDF_VERSION = '7'
    private val HEADER_MIN_VERSION_POSITION = 7
    private val HEADER_RANGE = 0..6
    private val HEADER_LENGTH = 7

    override fun checkFormat(bytes: ByteArray) {
        if (bytes.size < HEADER_LENGTH)
            throw InvalidFormatException("Downloaded content was not in the PDF format.")
        val minorVersion: Char = bytes[HEADER_MIN_VERSION_POSITION].toChar()
        val headerBytes: ByteArray = bytes.slice(HEADER_RANGE).toByteArray()
        val header = String(headerBytes, Charsets.UTF_8)
        val result = header == PDF_HEADER && minorVersion in MINIMUM_PDF_VERSION..MAXIMUM_PDF_VERSION
        if (!result)
            throw InvalidFormatException("Downloaded content was not in the PDF format.")
    }
}
