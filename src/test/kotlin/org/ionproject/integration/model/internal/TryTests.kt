package org.ionproject.integration.model.internal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TryTests {

    @Test
    fun whenOperationEndsInException_ThenTryHoldsFailure() {
        val tryResult = Try {
            "3.A".toInt()
        }
        assertTrue(tryResult.isFailure())
    }
    @Test
    fun whenOperationEndsInSuccess_ThenTryHoldsValueAndSuccess() {
        val tryResult: Try<Int> = Try {
            "2".toInt()
        }
        assertTrue(tryResult.isSuccess())
        assertEquals(2, tryResult.get())
    }
    @Test
    fun whenOperationIsPerformed_ThenItMatchesSuccess() {
        val tryResult = Try {
            1 / 0
        }
        val result = when (tryResult) {
            is Failure -> -1
            is Success -> tryResult.value
        }

        assertEquals(-1, result)
    }
    @Test
    fun whenOperationIsPerformed_ThenItMatchesFailure() {
        val tryResult = Try { 2 }

        val result = when (tryResult) {
            is Failure -> -1
            is Success -> tryResult.value
        }

        assertEquals(2, result)
    }
    @Test
    fun whenOperationResultsInException_ThenMapCanContinueToBeCalled() {
        val tryResult = Try { 2 }

        val tryResult2 = tryResult
            .map { it * 2 }
            .map { it.toString() + "t" }
            .map { it.toInt() }

        assertEquals(Failure<Int>((tryResult2 as Failure).e), tryResult2)
    }
    @Test
    fun whenFlatmapIsCalledOnACleanOperation_ThenItYieldsSuccess() {
        val tryResult = Try { 2 }

        val tryResult2 = tryResult
            .flatMap { i -> Try { i * 2 } }
            .flatMap { i -> Try { i.toString() } }

        assertEquals(Success("4"), tryResult2)
    }
    @Test
    fun whenFlatmapIsCalledWithException_ThenItYieldsFailure() {
        val tryResult = Try { 2 }

        val tryResult2 = tryResult
            .flatMap { i -> Try { i * 2 } }
            .flatMap { i -> Try { i.toString() + "t" } }
            .flatMap { i -> Try { i.toInt() } }

        assertTrue(tryResult2.isFailure())
    }

    @Test
    fun whenOperationResultsInFailure_ThenDefaultValueShouldBeObtained() {
        val tryResult = Try { 1 }

        assertEquals(tryResult.getOrElse(100), 1)

        val tryResult2 = Try { "3 sad tigers" }
            .map { it.toInt() }
            .getOrElse(100)

        assertEquals(100, tryResult2)
    }
    @Test
    fun whenOperationFails_ThenGetThrowsExcetion() {
        val tryResult = Try {
            throw RuntimeException("exception")
        }

        assertThrows<RuntimeException> { tryResult.get() }
    }
    @Test
    fun whenExceptionIsThrownOnTry_ThenOrElseShouldYieldDefault() {
        val tryResult: Try<Int> = Try {
            throw RuntimeException("exception")
        }

        assertEquals(1, tryResult.orElse(Try { 1 }).get())
    }
    @Test
    fun whenOperationIsSuccessful_ThenFoldShouldApplyFb() {
        val tryResult = Try { 1 }

        val result = tryResult.fold({ m -> m.message }, { n -> n.toString() })

        assertEquals("1", result)
    }
    @Test
    fun whenOperationFails_ThenFoldShouldApplyFa() {
        val tryResult = Try { throw RuntimeException("something") }

        val result = tryResult.fold({ m -> m.message }, { _ -> throw RuntimeException("else") })

        assertEquals("something", result)
    }
    @Test
    fun whenOperationIsSuccessfulButWithException_ThenFoldShouldApplyFa() {
        val tryResult = Try { 1 }

        val result = tryResult.fold({ m -> m.message }, { _ -> throw RuntimeException("something") })

        assertEquals("something", result)
    }
}
