package org.ionproject.integration.file

import java.io.IOException
import java.net.UnknownHostException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import org.ionproject.integration.file.`interface`.FileDownloader
import org.ionproject.integration.file.exception.InvalidFormatException
import org.ionproject.integration.file.implementation.PDFFileDownloader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class PDFFileDownloaderTest {

    companion object {

        private val pdfDownloader: FileDownloader = PDFFileDownloader()

        inline fun <reified T : Throwable> callDownloadAndAssertResultIsException(url: String, dstFile: String) {
            val ex = kotlin.runCatching { downloadPdf(url, dstFile)  }
            assert(ex.isFailure)
            assertTrue(ex.exceptionOrNull() is T)
        }
        fun downloadPdf(url: String, dstFile: String): Path {
            return pdfDownloader.download(url, dstFile)
        }
        fun deleteFile(path: Path) {
            path.toFile().delete()
        }
        fun assertFileDoesntExist(path : String) : Unit {
            val file =  Paths.get(path).toFile()
            assertFalse(file.exists())
        }
    }

    @Test
    fun whenValid_ThenDownloadIsSuccessful() {
        val dummyFileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        val dummyFileDst = "/tmp/dummy.pdf"
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf")

        val path = downloadPdf(dummyFileUrl, dummyFileDst)

        assertTrue(matcher.matches(path))
        assertEquals(String(path.toFile().readBytes().slice(0..6).toByteArray()),"%PDF-1.")
        deleteFile(path)
    }
    @Test
    fun whenContentIsntPdf_ThenThrowsInvalidArgumentException() {
        val remoteLocation = "https://www.google.pt"
        val notUsedPath = "/tmp/invalidArgument.pdf"
        callDownloadAndAssertResultIsException<InvalidFormatException>(remoteLocation, notUsedPath)
        assertFileDoesntExist(notUsedPath)
    }

    @Test
    fun whenHostDoesntExist_ThenThrowsUnknownHostException() {
        val dummyFileUrl = "https://www.oajsfaspfkl.com"
        val notUsedPath = "/tmp/unknownHost.pdf"
        callDownloadAndAssertResultIsException<UnknownHostException>(dummyFileUrl, notUsedPath)
        assertFileDoesntExist(notUsedPath)
    }
    @Test
    fun whenClientAsksForUnexistingResource_ThenThrowsIOException() {
        val dummyFileUrl = "http://getstatuscode.com/404"
        val notUsedPath = "/tmp/server404.pdf"
        callDownloadAndAssertResultIsException<IOException>(dummyFileUrl, notUsedPath)
        assertFileDoesntExist(notUsedPath)
    }
}
