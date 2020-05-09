

package org.ionproject.integration.file
import java.net.ConnectException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import org.ionproject.integration.file.interfaces.FileDownloader
import org.ionproject.integration.file.exceptions.InvalidFormatException
import org.ionproject.integration.file.exceptions.ServerErrorException
import org.ionproject.integration.file.implementations.PDFFileDownloader
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
internal class PDFFileDownloaderTest {
    companion object {
        private val pdfDownloader: FileDownloader = PDFFileDownloader()
        inline fun <reified T : Throwable> downloadAndAssertThrows(url: String, dstFile: String) {
            assertThrows<T> { downloadPdf(url, dstFile).orThrow() }
        }
        fun downloadPdf(url: String, dstFile: String): Try<Path> {
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
        val path = assertDoesNotThrow { downloadPdf(url, fileDst).orThrow() }
        assertTrue(matcher.matches(path))
        assertEquals(String(path.toFile().readBytes().slice(0..6).toByteArray()), "%PDF-1.")
        deleteFile(path)
    }
    @Test
    fun whenContentIsntPdf_ThenThrowsInvalidArgumentException() {
        val url = "https://www.google.pt"
        val fileDst = "/tmp/invalidArgument.pdf"
        downloadAndAssertThrows<InvalidFormatException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenHostDoesntExist_ThenThrowsConnectException() {
        val url = "https://www.oajsfaspfkl.com"
        val fileDst = "/tmp/unknownHost.pdf"
        downloadAndAssertThrows<ConnectException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenClientAsksForUnexistingResource_ThenThrowsInvalidFormatException() {
        val url = "http://google.com/i-on-project"
        val fileDst = "/tmp/server404.pdf"
        downloadAndAssertThrows<InvalidFormatException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenUrlIsNotPassed_ThenThrowsIllegalArgumentException() {
        val url = ""
        val fileDst = "/tmp/notUsedPath"
        downloadAndAssertThrows<IllegalArgumentException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenLocalPathIsNotPassed_ThenThrowsIllegalArgumentException() {
        val url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        val fileDst = ""
        downloadAndAssertThrows<IllegalArgumentException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
    @Test
    fun whenServerError_thenThrowsServerErrorException() {
        val url = "http://httpstat.us/500"
        val fileDst = "/tmp/notUsedPath"
        downloadAndAssertThrows<ServerErrorException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
}
