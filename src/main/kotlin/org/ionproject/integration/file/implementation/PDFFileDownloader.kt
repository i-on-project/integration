package org.ionproject.integration.file.implementation

class PDFFileDownloader : AbstractFileDownloader("PDF") {
    private val PDF_HEADER = "%PDF-1."
    private val MINIMUM_PDF_VERSION = '0'
    private val MAXIMUM_PDF_VERSION = '7'
    private val HEADER_MIN_VERSION_POSITION = 7
    private val HEADER_RANGE = 0..6

    override fun checkFormat(bytes: ByteArray): Boolean {
        val minorVersion: Char = bytes[HEADER_MIN_VERSION_POSITION].toChar()
        val headerBytes: ByteArray = bytes.slice(HEADER_RANGE).toByteArray()
        val header = String(headerBytes, Charsets.UTF_8)
        return header == PDF_HEADER && minorVersion in MINIMUM_PDF_VERSION..MAXIMUM_PDF_VERSION
    }
}
