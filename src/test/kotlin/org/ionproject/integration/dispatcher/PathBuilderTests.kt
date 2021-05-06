package org.ionproject.integration.dispatcher

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.lang.IllegalArgumentException

class PathBuilderTests {

    @Test
    fun `when given a relative path then success`() {
        val dirA = "test1ng"
        val dirB = "path_Builder"
        val dirC = "ok"

        val path = PathBuilder(dirA)
            .add(dirB)
            .add(dirC)
            .build()

        val expected = File(listOf(dirA, dirB, dirC).joinToString(File.separator))
        assertEquals(expected, path)
    }

    @Test
    fun `when given an absolute path then success`() {
        val dirA = "testing"
        val dirB = "pathBuilder"
        val dirC = "ok"

        val path = PathBuilder(dirA)
            .setPathType(PathBuilder.PathType.ABSOLUTE)
            .add(dirB)
            .add(dirC)
            .build()

        val expected = File(listOf(dirA, dirB, dirC).joinToString(File.separator, prefix = File.separator))
        assertEquals(expected, path)
    }

    @Test
    fun `when given an uppercase path then success`() {
        val dirA = "testing"
        val dirB = "pathBuilder"
        val dirC = "ok"

        val actual = PathBuilder(dirA)
            .setCaseType(PathBuilder.CaseType.UPPER)
            .add(dirB)
            .add(dirC)
            .build()

        val segments = listOf(dirA, dirB, dirC).map(String::uppercase)
        val expected = File(segments.joinToString(File.separator))
        assertEquals(expected.path, actual.path)
    }

    @Test
    fun `when given a lowercase path then success`() {
        val dirA = "TESTING"
        val dirB = "pathBuilder"
        val dirC = "ok"

        val path = PathBuilder(dirA)
            .setCaseType(PathBuilder.CaseType.LOWER)
            .add(dirB)
            .add(dirC)
            .build()

        val segments = listOf(dirA, dirB, dirC).map(String::lowercase)
        val expected = File(segments.joinToString(File.separator))
        assertEquals(expected.path, path.path)
    }

    @Test
    fun `when given a Lowercase with leading and trailing space path then success`() {
        val dirA = "   TESTING "
        val dirB = " pathBuilder             "
        val dirC = "ok  "

        val path = PathBuilder(dirA)
            .add(dirB)
            .add(dirC)
            .setCaseType(PathBuilder.CaseType.LOWER)
            .build()

        val segments = listOf(dirA, dirB, dirC).map(String::lowercase).map(String::trim)
        val expected = File(segments.joinToString(File.separator))
        assertEquals(expected.path, path.path)
    }

    @Test
    fun `when given segments with leading space then trim and success`() {
        val dirA = "   testing"
        val dirB = "pathBuilder\n\r"
        val dirC = "     ok "

        val path = PathBuilder(dirA)
            .add(dirB)
            .setPathType(PathBuilder.PathType.ABSOLUTE)
            .add(dirC)
            .build()

        val segments = listOf(dirA, dirB, dirC).map(String::trim)
        val expected = File(segments.joinToString(File.separator, prefix = File.separator))
        assertEquals(expected, path)
    }

    @Test
    fun `when given an absolute and uppercase path then success`() {
        val dirA = "testing"
        val dirB = "pathBuilder"
        val dirC = "ok"

        val path = PathBuilder(dirA)
            .setPathType(PathBuilder.PathType.ABSOLUTE)
            .add(dirB)
            .add(dirC)
            .setCaseType(PathBuilder.CaseType.UPPER)
            .build()

        val segments = listOf(dirA, dirB, dirC).map(String::uppercase)
        val expected = File(segments.joinToString(File.separator, prefix = File.separator))
        assertEquals(expected, path)
    }

    @Test
    fun `when given an empty segment path then fail`() {
        val dirA = "testing"
        val dirB = "pathBuilder"
        val dirC = ""

        assertThrows<IllegalArgumentException> {
            PathBuilder(dirA)
                .setPathType(PathBuilder.PathType.ABSOLUTE)
                .add(dirB)
                .add(dirC)
                .build()
        }
    }

    @Test
    fun `when given an empty root path then fail`() {
        val dirA = ""
        val dirB = "pathBuilder"
        val dirC = "ok"

        assertThrows<IllegalArgumentException> {
            PathBuilder(dirA)
                .setPathType(PathBuilder.PathType.ABSOLUTE)
                .add(dirB)
                .add(dirC)
                .build()
        }
    }

    @Test
    fun `when given an invalid path with newline then fail`() {
        val dirA = "aaa"
        val dirB = "path\nBuilder"
        val dirC = "ok"

        assertThrows<IllegalArgumentException> {
            PathBuilder(dirA)
                .setPathType(PathBuilder.PathType.ABSOLUTE)
                .add(dirB)
                .add(dirC)
                .build()
        }
    }

    @Test
    fun `when given an invalid path with illegal characters then fail`() {
        val dirA = "aa<a"
        val dirB = "pathBuilder"
        val dirC = "ok"

        assertThrows<IllegalArgumentException> {
            PathBuilder(dirA)
                .setPathType(PathBuilder.PathType.ABSOLUTE)
                .add(dirB)
                .add(dirC)
                .build()
        }
    }
}
