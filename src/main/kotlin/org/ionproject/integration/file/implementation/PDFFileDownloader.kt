package org.ionproject.integration.file.implementation

class PDFFileDownloader : AbstractFileDownloader("PDF") {
    private val PDF_HEADER = "%PDF-1."
    private val MIN_PDF_VERSION = '1'
    private val MAX_PDF_VERSION = '7'

    override fun checkFormat(bytes: ByteArray): Boolean {
        val version: Char = bytes[7].toChar()
        val headerBytes: ByteArray = bytes.slice(0..6).toByteArray()
        val header = String(headerBytes, Charsets.UTF_8)
        return header == PDF_HEADER && version in MIN_PDF_VERSION..MAX_PDF_VERSION
    }
}
