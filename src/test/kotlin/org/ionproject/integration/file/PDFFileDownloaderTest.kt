package org.ionproject.integration.file

import java.io.IOException
import java.net.UnknownHostException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import org.ionproject.integration.file.`interface`.FileDownloader
import org.ionproject.integration.file.exception.InvalidFormatException
import org.ionproject.integration.file.implementation.PDFFileDownloader
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PDFFileDownloaderTest {

    companion object {

        val pdfDownloader: FileDownloader = PDFFileDownloader()

        inline fun <reified T : Throwable> downloadPdfAndAssertThrows(url: String, dstFile: String) {
            assertThrows<T> {
                pdfDownloader.download(url, dstFile)
            }
        }
        fun downloadPdf(url: String, dstFile: String): Path {
            return pdfDownloader.download(url, dstFile)
        }
        fun deleteFile(path: Path) {
            path.toFile().delete()
        }
    }
    @Test
    fun whenValidThenDownloadIsSuccessful() {
        val dummyFileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        val dummyFileDst = "/tmp/dummy.pdf"
        val path = downloadPdf(dummyFileUrl, dummyFileDst)
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf")
        assertTrue(matcher.matches(path))
        deleteFile(path)
    }
    @Test
    fun whenContentIsntPdfThenThrowsInvalidArgumentException() {
        val remoteLocation = "https://www.google.pt"
        val notUsedPath = "/tmp/invalidArgument.pdf"
        downloadPdfAndAssertThrows<InvalidFormatException>(remoteLocation, notUsedPath)
    }

    @Test
    fun whenHostDoesntExistThrowsUnknownHostException() {
        val dummyFileUrl = "https://www.oajsfaspfkl.com"
        val notUsedPath = "/tmp/unknownHost.pdf"
        downloadPdfAndAssertThrows<UnknownHostException>(dummyFileUrl, notUsedPath)
    }
    @Test
    fun whenServiceIsUnavailableThrowsIOException() {
        val dummyFileUrl = "http://getstatuscode.com/404"
        val notUsedPath = "/tmp/server403.pdf"
        downloadPdfAndAssertThrows<IOException>(dummyFileUrl, notUsedPath)
    }
}
