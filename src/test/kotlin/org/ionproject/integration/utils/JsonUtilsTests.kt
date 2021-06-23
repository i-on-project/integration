package org.ionproject.integration.utils

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Types
import org.ionproject.integration.infrastructure.JsonUtils
import org.ionproject.integration.model.Simple
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JsonUtilsTests {
    companion object {
        val type = Types.getRawType(Simple::class.java)

        fun fromJson(json: String): Try<Simple> {
            return JsonUtils.fromJson(json, type)
        }
    }

    @Test
    fun whenValidJson_thenReturnSimpleObject() {
        // Arrange
        val json = "{\"id\": 1, \"description\":\"abc\"}"

        // Act
        val simple = fromJson(json)
            .orThrow()

        // Assert
        assertEquals(1, simple.id)
        assertEquals("abc", simple.description)
    }

    @Test
    fun whenJsonDoesntConformToSchema_thenReturnsException() {
        // Arrange
        val json = "{\"x\": 1, \"y\":\"abc\"}"

        // Act
        val simple = fromJson(json)

        // Assert
        val e = assertThrows<CompositeException> { simple.orThrow() }
        assertEquals(2, e.exceptions.count())
        assertEquals(true, e.exceptions[1] is JsonDataException)
        assertEquals("Invalid json", e.exceptions[1].message)
    }

    @Test
    fun whenNonJsonString_thenReturnsException() {
        // Arrange
        val json = "test"

        // Act
        val simple = fromJson(json)

        // Assert
        val e = assertThrows<CompositeException> { simple.orThrow() }
        assertEquals(2, e.exceptions.count())
        assertEquals(true, e.exceptions[1] is JsonDataException)
        assertEquals("Invalid json", e.exceptions[1].message)
    }
}
