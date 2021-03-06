package org.ionproject.integration.infrastructure

import org.ionproject.integration.infrastructure.Try.Companion.ofValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TryTests {

    private fun oper(a: Int, b: Int) =
        Try.of { a / b }
            .map { it + 1 }
            .map { it * 2 }

    @Test
    fun whenNonZeroOperands_thenReturnsArithmeticResult() {
        // Act
        val result = oper(2, 1)
            .match({ it.toString() }, { it.message })

        // Assert
        assertEquals("6", result)
    }

    @Test
    fun whenNonZeroOperandsAndDefaultError_thenReturnsArithmeticResult() {
        // Act
        val result = oper(2, 1)
            .map { it.toString() }
            .orElse("error")

        // Assert
        assertEquals("6", result)
    }

    @Test
    fun whenNonZeroOperandsAndDefaultThrow_thenReturnsArithmeticResult() {
        // Act
        val result = oper(2, 1)
            .map { it.toString() }

        // Assert
        assertDoesNotThrow { result.orThrow() }
        assertEquals("6", result.orThrow())
    }

    @Test
    fun whenDivideByZero_thenReturnsErrorWithArithmeticExceptionMessage() {
        // Act
        val result = oper(1, 0)
            .match({ it.toString() }, { it.message })

        // Assert
        assertTrue("/ by zero" == result || "divide by zero" == result)
    }

    @Test
    fun whenDivideByZeroAndDefaultError_thenReturnsDefaultError() {
        // Act
        val result = oper(1, 0)
            .map { it.toString() }
            .orElse("error")

        // Assert
        assertEquals("error", result)
    }

    @Test
    fun whenDivideByZeroAndDefaultThrow_thenThrowsArithmeticException() {
        // Act
        val result = oper(1, 0)
            .map { it.toString() }

        // Assert
        assertThrows<ArithmeticException> { result.orThrow() }
    }

    @Test
    fun whenOfValue_thenValueInstance() {
        // Act
        val result = ofValue(1)

        // Assert
        assertEquals(1, result.orThrow())
    }

    @Test
    fun whenOfError_thenErrorInstance() {
        // Act
        val result = Try.ofError<Exception>(Exception())

        // Assert
        assertThrows<Exception> { result.orThrow<Exception>() }
    }

    @Test
    fun whenMultipleOperWithNonZeroOperands_thenReturnsArithmeticResult() {
        // Act
        val result = Try.map(oper(2, 1), oper(2, 1)) { a, b -> (a + b).toString() }
            .orElse { it.javaClass.simpleName }

        // Assert
        assertEquals("12", result)
    }

    @Test
    fun whenMultipleOperOneDivideByZero_thenReturnsErrorWithArithmeticExceptionMessage() {
        // Act
        val result = Try.map(oper(2, 1), oper(1, 0)) { a, b -> (a + b).toString() }
            .orElse { it.javaClass.simpleName }

        // Assert
        assertEquals("ArithmeticException", result)
    }

    @Test
    fun whenMultipleOperAllDivideByZero_thenReturnsErrorWithCompositeExceptionMessage() {
        // Act
        val result = Try.map(oper(1, 0), oper(1, 0)) { a, b -> (a + b).toString() }
            .orElse { it.javaClass.simpleName }

        // Assert
        assertEquals("CompositeException", result)
    }

    @Test
    fun whenNonZeroOperandsAndMapError_thenReturnsArithmeticResult() {
        // Act
        val result = oper(2, 1)
            .mapError { NullPointerException("NullPointer Exception") }
            .match({ it.toString() }, { it.message })

        // Assert
        assertEquals("6", result)
    }

    @Test
    fun whenDivideByZeroAndMapError_thenReturnsErrorWithCompositeMessage() {
        // Act
        val result = oper(1, 0)
            .mapError { NullPointerException("NullPointer Exception") }

        // Assert
        val e = assertThrows<CompositeException> { result.orThrow() }
        assertEquals(2, e.exceptions.count())
        assertEquals(true, e.exceptions[0] is ArithmeticException)
        assertEquals(true, e.exceptions[1] is NullPointerException)
    }

    @Test
    fun `map using multiple int arguments and return result`() {
        val result = Try.map(ofValue(1), ofValue(3), ofValue(2)) { it.sum() }
            .orElse { it.javaClass.simpleName }

        assertEquals(6, result)
    }

    @Test
    fun `map using multiple string arguments and return result`() {
        val result = Try.map(
            ofValue("foo"),
            ofValue("bar"),
            ofValue("baz"),
            ofValue("!")
        ) { it.joinToString(separator = " ") }
            .orElse { it.javaClass.simpleName }

        assertEquals("foo bar baz !", result)
    }

    @Test
    fun `map using multiple int arguments and throwing arithmetic exception when dividing by zero`() {
        val result =
            Try.map(oper(1, 1), oper(122, 0), oper(3, 0), oper(-1, 0)) { it.sum() }

        val e = assertThrows<CompositeException> { result.orThrow() }
        assertEquals(3, e.exceptions.count())
        assertTrue { e.exceptions.all { it is ArithmeticException } }
    }
}
