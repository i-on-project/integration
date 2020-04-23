package org.ionproject.integration.file

import org.ionproject.integration.file.`interface`.FileDownloader
import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import org.ionproject.integration.file.exception.InvalidFormatException
import org.ionproject.integration.file.implementation.PDFFileDownloader
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PDFFileDownloaderTest {

    val LOCAL_FILE_DESTINATION = "/tmp/dummy.pdf"
    @Test
    fun whenValidThenDownloadIsSuccessful() {
        val dummyFileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        val fd: FileDownloader = PDFFileDownloader()
        val path = fd.download(dummyFileUrl, LOCAL_FILE_DESTINATION)
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf")
        assertTrue(matcher.matches(path))
    }
    @Test
    fun whenContentIsntPdfThenThrowsInvalidArgumentException() {
        val remoteLocation = "https://www.google.pt"
        val fd: FileDownloader = PDFFileDownloader()
        assertThrows<InvalidFormatException> {
            fd.download(remoteLocation, LOCAL_FILE_DESTINATION)
        }
    }

}
