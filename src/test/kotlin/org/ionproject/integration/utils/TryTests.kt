package org.ionproject.integration.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TryTests {

    fun oper(a: Int, b: Int) =
        Try.of { a / b }
            .map { it + 1 }
            .map { it * 2 }

    @Test
    fun test() {
        assertEquals("/ by zero", oper(1, 0).match({ it.toString() }, { it.message }))
        assertEquals("6", oper(2, 1).match({ it.toString() }, { it.message }))
        assertEquals("error", oper(1, 0).map { it.toString() }.orElse("error"))
        assertEquals("6", oper(2, 1).map { it.toString() }.orElse("error"))

        assertEquals("CompositeException",
            Try.map(oper(1, 0), oper(1, 0)) { a, b -> (a + b).toString() }.orElse { it.javaClass.simpleName })
        assertEquals("ArithmeticException",
            Try.map(oper(2, 1), oper(1, 0)) { a, b -> (a + b).toString() }.orElse { it.javaClass.simpleName })
        assertEquals("ArithmeticException",
            Try.map(oper(1, 0), oper(2, 1)) { a, b -> (a + b).toString() }.orElse { it.javaClass.simpleName })
        assertEquals("12",
            Try.map(oper(2, 1), oper(2, 1)) { a, b -> (a + b).toString() }.orElse { it.javaClass.simpleName })
    }
}
