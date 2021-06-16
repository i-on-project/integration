package org.ionproject.integration.infrastructure.http

import org.ionproject.integration.utils.CompositeException
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths

class HttpsClientServiceTests {
    companion object {
        private val httpClient = HttpsClientService()
        inline fun <reified T : Throwable> downloadThrows(uri: URI, dstFile: Path): T {
            return assertThrows<T> {
                downloadPdf(
                    uri,
                    dstFile
                ).orThrow()
            }
        }

        fun downloadPdf(uri: URI, dstFile: Path): Try<Path> {
            return httpClient.download(uri, dstFile)
        }

        fun deleteFile(path: Path) {
            path.toFile().delete()
        }

        fun assertFileDoesntExist(path: Path) {
            val file = path.toFile()
            assertFalse(file.exists())
        }
    }

    @Test
    fun whenValid_ThenDownloadIsSuccessful() {
        val uri = URI.create("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
        val fileDst = File("dummy.pdf").toPath()
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf")
        try {
            val path = assertDoesNotThrow {
                downloadPdf(
                    uri,
                    fileDst
                ).orThrow()
            }
            assertTrue(matcher.matches(path))
            assertEquals(String(path.toFile().readBytes().slice(0..6).toByteArray()), "%PDF-1.")
        } finally {
            deleteFile(
                fileDst
            )
        }
    }

    @Test
    fun whenHostDoesntExist_ThenThrowsConnectException() {
        val uri = URI.create("https://www.oajsfaspfkl.com")
        val fileDst = Paths.get("/tmp/unknownHost.pdf")
        val cEx =
            downloadThrows<CompositeException>(
                uri,
                fileDst
            )
        assertEquals("ConnectException", cEx.exceptions[0]::class.java.simpleName)
        assertFileDoesntExist(
            fileDst
        )
    }

/*    @Test
    @Ignore
    fun whenClientAsksForUnexistingResource_ThenThrowsFileNotFoundException() {
        val uri = URI.create("http://google.com/i-on-project")
        val fileDst = Paths.get("/tmp/server404.pdf")
        downloadThrows<FileNotFoundException>(
            uri,
            fileDst
        )
        assertFileDoesntExist(
            fileDst
        )
    }*/

    @Test
    fun whenUrlIsNotPassed_ThenThrowsIllegalArgumentException() {
        val uri = URI.create("")
        val fileDst = Paths.get("/tmp/notUsedPath")
        downloadThrows<IllegalArgumentException>(
            uri,
            fileDst
        )
        assertFileDoesntExist(
            fileDst
        )
    }

    @Test
    fun whenLocalPathIsNotPassed_ThenThrowsIllegalArgumentException() {
        val uri = URI.create("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
        val fileDst = Paths.get("")
        downloadThrows<IllegalArgumentException>(
            uri,
            fileDst
        )
        assertFileDoesntExist(
            fileDst
        )
    }

    @Test
    fun whenServerError_thenThrowsServerErrorException() {
        val url = URI.create("http://httpstat.us/500")
        val fileDst = Paths.get("/tmp/notUsedPath")
        val cEx =
            downloadThrows<CompositeException>(
                url,
                fileDst
            )
        assertEquals("ServerErrorException", cEx.exceptions[0]::class.java.simpleName)
        assertFileDoesntExist(
            fileDst
        )
    }
}
