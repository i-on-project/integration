package org.ionproject.integration.file

import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import org.ionproject.integration.file.exception.InvalidFormatException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PDFFileDownloaderTest {

    val LOCAL_FILE_DESTINATION = "/tmp/dummy.pdf"
    @Test
    fun testDownloadSuccessful() {
        val dummyFileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        val fd: FileDownloader = PDFFileDownloader()
        val path = fd.download(dummyFileUrl, LOCAL_FILE_DESTINATION)
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf")
        assertTrue(matcher.matches(path))
    }
    @Test
    fun testDownloadUnsuccessful() {
        val remoteLocation = "https://www.google.pt"
        val fd: FileDownloader = PDFFileDownloader()
        assertThrows<InvalidFormatException> {
            fd.download(remoteLocation, LOCAL_FILE_DESTINATION)
        }
    }
}
