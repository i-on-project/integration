package org.ionproject.integration.file.implementations

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileNotFoundException

internal class FileDigestImplTest {
    companion object {
        val fd = FileDigestImpl()
    }

    @Test
    fun whenFileExists_thenDigestIsSuccessfullyCalculated() {
        val file = File("src/test/resources/test.pdf")
        val expected = byteArrayOf(
            -12, -40, -8, -95, -114, -63, -71, -81, -108, -17, 0, -65, -58, 72, 120,
            -44, 48, 8, 124, -3, -67, 58, -70, -95, -53, -128, -125, 0, 119, 81, 66, -74
        )
        val digest = fd.digest(file)
        assertTrue(digest.contentEquals(expected))
    }

    @Test
    fun whenFileDoesntExist_thenThrowFileNotFoundException() {
        val file = File("src/test/resources/non-existant.pdf")
        val ex = assertThrows<FileNotFoundException> { fd.digest(file) }
        assertTrue(
            "${file.path} (No such file or directory)" == ex.message ||
                "${file.path} (The system cannot find the file specified)" == ex.message
        )
    }

    @Test
    fun whenFileIsDirectory_thenThrowFileNotFoundException() {
        val file = File("src/test/resources")
        val ex = assertThrows<FileNotFoundException> { fd.digest(file) }
        assertTrue(
            "${file.path} (Is a directory)" == ex.message ||
                "${file.path} (Access is denied)" == ex.message
        )
    }
}
