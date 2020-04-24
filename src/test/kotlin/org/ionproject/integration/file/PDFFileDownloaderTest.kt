package org.ionproject.integration.file

import java.io.IOException
import java.net.UnknownHostException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import org.ionproject.integration.file.`interface`.FileDownloader
import org.ionproject.integration.file.exception.InvalidFormatException
import org.ionproject.integration.file.exception.ServerErrorException
import org.ionproject.integration.file.implementation.PDFFileDownloader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PDFFileDownloaderTest {

    companion object {

        private val pdfDownloader: FileDownloader = PDFFileDownloader()

        inline fun <reified T : Throwable> callDownloadAndAssertResultIsException(url: String, dstFile: String) {
            val ex = kotlin.runCatching { downloadPdf(url, dstFile) }
            assert(ex.isFailure)
            assertTrue(ex.exceptionOrNull() is T)
        }
        fun downloadPdf(url: String, dstFile: String): Path {
            return pdfDownloader.download(url, dstFile)
        }
        fun deleteFile(path: Path) {
            path.toFile().delete()
        }
        fun assertFileDoesntExist(path: String) {
            val file = Paths.get(path).toFile()
            assertFalse(file.exists())
        }
    }

    @Test
    fun whenValid_ThenDownloadIsSuccessful() {
        val url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        val fileDst = "/tmp/dummy.pdf"
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf")

        val path = downloadPdf(url, fileDst)

        assertTrue(matcher.matches(path))
        assertEquals(String(path.toFile().readBytes().slice(0..6).toByteArray()), "%PDF-1.")

        deleteFile(path)
    }
    @Test
    fun whenContentIsntPdf_ThenThrowsInvalidArgumentException() {
        val url = "https://www.google.pt"
        val fileDst = "/tmp/invalidArgument.pdf"
        callDownloadAndAssertResultIsException<InvalidFormatException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenHostDoesntExist_ThenThrowsUnknownHostException() {
        val url = "https://www.oajsfaspfkl.com"
        val fileDst = "/tmp/unknownHost.pdf"
        callDownloadAndAssertResultIsException<UnknownHostException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenClientAsksForUnexistingResource_ThenThrowsIOException() {
        val url = "http://getstatuscode.com/404"
        val fileDst = "/tmp/server404.pdf"
        callDownloadAndAssertResultIsException<IOException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenUrlIsNotPassed_ThenThrowsIllegalArgumentException() {
        val url = ""
        val fileDst = "/tmp/notUsedPath"
        callDownloadAndAssertResultIsException<IllegalArgumentException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenLocalPathIsNotPassed_ThenThrowsIllegalArgumentException() {
        val url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        val fileDst = ""
        callDownloadAndAssertResultIsException<IllegalArgumentException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenServerError_thenThrowsServerErrorException() {
        val url = "http://httpstat.us/500"
        val fileDst = "/tmp/notUsedPath"
        callDownloadAndAssertResultIsException<ServerErrorException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
}
