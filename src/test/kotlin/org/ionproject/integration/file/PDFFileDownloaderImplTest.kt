package org.ionproject.integration.file

import java.net.ConnectException
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import org.ionproject.integration.file.exceptions.InvalidFormatException
import org.ionproject.integration.file.exceptions.ServerErrorException
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.file.interfaces.FileDownloader
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class PDFFileDownloaderImplTest {
    companion object {
        private val checker = PDFBytesFormatChecker()
        private val pdfDownloader: FileDownloader = FileDownloaderImpl(checker)
        inline fun <reified T : Throwable> downloadAndAssertThrows(uri: URI, dstFile: String) {
            assertThrows<T> { downloadPdf(uri, dstFile).orThrow() }
        }

        fun downloadPdf(uri: URI, dstFile: String): Try<Path> {
            return pdfDownloader.download(uri, dstFile)
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
        val uri = URI.create("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
        val fileDst = "/tmp/dummy.pdf"
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf")
        try {
            val path = assertDoesNotThrow { downloadPdf(uri, fileDst).orThrow() }
            assertTrue(matcher.matches(path))
            assertEquals(String(path.toFile().readBytes().slice(0..6).toByteArray()), "%PDF-1.")
        } finally {
            deleteFile(Paths.get(fileDst))
        }
    }

    @Test
    fun whenContentIsntPdf_ThenThrowsInvalidArgumentException() {
        val uri = URI.create("https://www.google.pt")
        val fileDst = "/tmp/invalidArgument.pdf"
        downloadAndAssertThrows<InvalidFormatException>(uri, fileDst)
        assertFileDoesntExist(fileDst)
    }

    @Test
    fun whenHostDoesntExist_ThenThrowsConnectException() {
        val uri = URI.create("https://www.oajsfaspfkl.com")
        val fileDst = "/tmp/unknownHost.pdf"
        downloadAndAssertThrows<ConnectException>(uri, fileDst)
        assertFileDoesntExist(fileDst)
    }

    @Test
    fun whenClientAsksForUnexistingResource_ThenThrowsInvalidFormatException() {
        val uri = URI.create("http://google.com/i-on-project")
        val fileDst = "/tmp/server404.pdf"
        downloadAndAssertThrows<InvalidFormatException>(uri, fileDst)
        assertFileDoesntExist(fileDst)
    }

    @Test
    fun whenUrlIsNotPassed_ThenThrowsIllegalArgumentException() {
        val uri = URI.create("")
        val fileDst = "/tmp/notUsedPath"
        downloadAndAssertThrows<IllegalArgumentException>(uri, fileDst)
        assertFileDoesntExist(fileDst)
    }

    @Test
    fun whenLocalPathIsNotPassed_ThenThrowsIllegalArgumentException() {
        val uri = URI.create("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
        val fileDst = ""
        downloadAndAssertThrows<IllegalArgumentException>(uri, fileDst)
        assertFileDoesntExist(fileDst)
    }

    @Test
    fun whenServerError_thenThrowsServerErrorException() {
        val url = URI.create("http://httpstat.us/500")
        val fileDst = "/tmp/notUsedPath"
        downloadAndAssertThrows<ServerErrorException>(url, fileDst)
        assertFileDoesntExist(fileDst)
    }
}
