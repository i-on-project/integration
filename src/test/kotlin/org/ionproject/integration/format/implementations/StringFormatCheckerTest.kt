package org.ionproject.integration.format.implementations

import org.ionproject.integration.infrastructure.StringFormatChecker
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class StringFormatCheckerTest {

    companion object {
        private val sfc = StringFormatChecker("\\bBob\\b \\d{3}")

        fun stringMatchesPattern(string: String): Boolean {
            return sfc.checkFormat(string)
        }
    }

    @Test
    fun whenIsExactMath_ThenReturnTrue() {
        val content = "Bob 123"
        val result = stringMatchesPattern(content)
        assertTrue(result)
    }
    @Test
    fun whenIsPartialMatch_ThenReturnTrue() {
        val content = "Alice Bob 1234"
        val result = stringMatchesPattern(content)
        assertTrue(result)
    }
    @Test
    fun whenStringIsEmpty_ThenReturnFalse() {
        val content = ""
        val result = stringMatchesPattern(content)
        assertFalse(result)
    }
    @Test
    fun whenDoesNotMatchEntirely_ThenReturnFalse() {
        val content = "The quick brown fox jumps over the lazy dog"
        val result = stringMatchesPattern(content)
        assertFalse(result)
    }
}
