package org.ionproject.integration.dispatcher

import org.ionproject.integration.infrastructure.file.Filepath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class FilepathTests {

    @Test
    fun `when given a relative path then success`() {
        val dirA = "test1ng"
        val dirB = "path_Builder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        val actual = Filepath(segments).asFile

        val expected = File(segments.joinToString(File.separator))
        assertEquals(expected, actual)
    }

    @Test
    fun `when given an absolute path then success`() {
        val dirA = "testing"
        val dirB = "pathBuilder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        val actual = Filepath(segments, Filepath.PathType.ABSOLUTE).asFile
        val expected = File(segments.joinToString(File.separator, prefix = File.separator))

        assertEquals(expected, actual)
    }

    @Test
    fun `when given an uppercase path then success`() {
        val dirA = "testing"
        val dirB = "pathBuilder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        val actual = Filepath(segments, caseType = Filepath.CaseType.UPPER)
        val expected = File(segments.joinToString(File.separator, transform = String::uppercase))

        assertEquals(expected.path, actual.path)
    }

    @Test
    fun `when given a lowercase path then success`() {
        val dirA = "TESTING"
        val dirB = "pathBuilder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        val actual = Filepath(segments, caseType = Filepath.CaseType.LOWER)
        val expected = File(segments.joinToString(File.separator, transform = String::lowercase))

        assertEquals(expected.path, actual.path)
    }

    @Test
    fun `when given a Lowercase with leading and trailing space path then success`() {
        val dirA = "   TESTING "
        val dirB = " pathBuilder             "
        val dirC = "ok  "
        val segments = listOf(dirA, dirB, dirC)

        val actual = Filepath(segments, caseType = Filepath.CaseType.LOWER)

        val expected = File(segments.map(String::lowercase).joinToString(File.separator, transform = String::trim))
        assertEquals(expected.path, actual.path)
    }

    @Test
    fun `when given segments with leading space then trim and success`() {
        val dirA = "   testing"
        val dirB = "pathBuilder\n\r"
        val dirC = "     ok "
        val segments = listOf(dirA, dirB, dirC)

        val actual = Filepath(segments, Filepath.PathType.ABSOLUTE).asFile
        val expected = File(segments.joinToString(File.separator, prefix = File.separator, transform = String::trim))

        assertEquals(expected, actual)
    }

    @Test
    fun `when given an absolute and uppercase path then success`() {
        val dirA = "testing"
        val dirC = "ok"
        val segments = listOf(dirA, dirC)

        val actual = Filepath(segments, Filepath.PathType.ABSOLUTE, Filepath.CaseType.UPPER).asFile
        val expected = File(
            segments.joinToString(
                File.separator,
                prefix = File.separator,
                transform = String::uppercase
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `when given root only path then success`() {
        val dirA = "testing"

        val path = Filepath(listOf(dirA)).asFile
        val expected = File(dirA)

        assertEquals(expected, path)
    }

    @Test
    fun `when adding a Filepath to a string then success`() {
        val dirA = "test1ng"
        val dirB = "path_Builder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        val someFile = "file.txt"
        val actual = Filepath(segments) + someFile

        val allSegments = segments + someFile
        val expected = File(allSegments.joinToString(File.separator))

        assertEquals(expected, actual.asFile)
    }

    @Test
    fun `when adding a Filepath to an empty string then fail`() {
        val dirA = "test1ng"
        val dirB = "path_Builder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        assertThrows<IllegalArgumentException> {
            Filepath(segments) + ""
        }
    }

    @Test
    fun `when given an empty segment path then fail`() {
        val dirA = "testing"
        val dirB = "pathBuilder"
        val dirC = ""
        val segments = listOf(dirA, dirB, dirC)

        assertThrows<IllegalArgumentException> {
            Filepath(segments, Filepath.PathType.ABSOLUTE)
        }
    }

    @Test
    fun `when given an empty root path then fail`() {
        val dirA = ""
        val dirB = "pathBuilder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        assertThrows<IllegalArgumentException> {
            Filepath(segments)
        }
    }

    @Test
    fun `when given an invalid path with newline then fail`() {
        val dirA = "aaa"
        val dirB = "path\nBuilder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        assertThrows<IllegalArgumentException> {
            Filepath(segments)
        }
    }

    @Test
    fun `when given an invalid path with illegal characters then fail`() {
        val dirA = "aa<a"
        val dirB = "pathBuilder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        assertThrows<IllegalArgumentException> {
            Filepath(segments)
        }
    }

    @Test
    fun `when given an empty path list then fail`() {
        assertThrows<IllegalArgumentException> {
            Filepath(emptyList())
        }
    }

    @Test
    fun `when adding a filepath to a list of strings then build new filepath`() {
        val dirA = "aaa"
        val dirB = "pathBuilder"
        val dirC = "ok"
        val segments = listOf(dirA, dirB, dirC)

        val original = Filepath(segments)
        val actual = original + listOf("more", "segments")

        val expected =
            segments.joinToString(separator = File.separator) + "${File.separator}more${File.separator}segments"

        assertEquals(expected, actual.path)
    }
}
