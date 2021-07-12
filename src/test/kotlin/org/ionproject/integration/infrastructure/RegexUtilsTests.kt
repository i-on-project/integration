package org.ionproject.integration.infrastructure

import org.ionproject.integration.infrastructure.text.RegexUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RegexUtilsTests {

    companion object {
        private const val match_pattern = "\\d{3}"
        private const val matches_pattern = "^(Undergraduate|Masters).*\$"

        fun isMatch(string: String): Boolean {
            return RegexUtils.isMatch(match_pattern, string)
        }

        fun findMatches(string: String, regexOption: RegexOption = RegexOption.IGNORE_CASE): List<String> {
            return RegexUtils.findMatches(matches_pattern, string, regexOption)
        }
    }

    @Test
    fun whenFullyMatch_thenReturnsTrue() {
        // Arrange
        val s = "123"

        // Act
        val match = isMatch(s)

        // Assert
        assertEquals(true, match)
    }

    @Test
    fun whenMultipleMatch_thenReturnsTrue() {
        // Arrange
        val s = "abc 123 def 456"

        // Act
        val match = isMatch(s)

        // Assert
        assertEquals(true, match)
    }

    @Test
    fun whenDoesntMatch_thenReturnsFalse() {
        // Arrange
        val s = "abcd"

        // Act
        val match = isMatch(s)

        // Assert
        assertEquals(false, match)
    }

    @Test
    fun whenMatch_thenReturnsListAtLeastOneFullMatch() {
        // Arrange
        val s = "Undergraduate"

        // Act
        val matches = findMatches(s)

        // Assert
        assertTrue(matches.count() >= 1)
    }

    @Test
    fun whenDoesntMatch_thenReturnsEmptyList() {
        // Arrange
        val s = "HighSchool"

        // Act
        val matches = findMatches(s)

        // Assert
        assertEquals(0, matches.count())
    }

    @Test
    fun whenMatchMultiline_andUseRegexOption_thenReturnsListAtLeastTwoFullMatches() {
        // Arrange
        val s = "Undergraduate\nHighSchool\nMasters\nPhD"

        // Act
        val matches = findMatches(s, RegexOption.MULTILINE)

        // Assert
        assertTrue(matches.count() >= 2)
    }

    @Test
    fun whenMatchMultiline_andDefaultRegexOption_thenReturnsEmptyList() {
        // Arrange
        val s = "Undergraduate\nHighSchool\nMasters\nPhD"

        // Act
        val matches = findMatches(s)

        // Assert
        assertEquals(0, matches.count())
    }
}
