package org.ionproject.integration.format.implementations

import com.squareup.moshi.Types
import org.ionproject.integration.infrastructure.text.JsonFormatChecker
import org.ionproject.integration.model.Nested
import org.ionproject.integration.model.Simple
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class JsonFormatCheckerTest {

    companion object {
        private val dummyJfc = JsonFormatChecker<Simple>(Types.getRawType(Simple::class.java))
        fun matchesSimpleSchema(jsonString: String): Boolean {
            return dummyJfc.checkFormat(jsonString)
        }
        private val dummyListJfc = JsonFormatChecker<List<Simple>>(Types.newParameterizedType(List::class.java, Simple::class.java))
        fun matchesArrayOfSimpleSchema(jsonString: String): Boolean {
            return dummyListJfc.checkFormat(jsonString)
        }
        private val nestedObjectJfc = JsonFormatChecker<Nested>(Nested::class.java)
        fun matchesNestedSchema(jsonString: String): Boolean {
            return nestedObjectJfc.checkFormat(jsonString)
        }
    }
    @Test
    fun whenIsInvalidJson_ThenResultIsFalse() {
        val content = "{\"id\": 1} description\":\"abc\"}"
        val result = matchesSimpleSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenSimpleTypeMatches_ThenResultIsTrue() {
        val content = "{\"id\": 1, \"description\":\"abc\"}"
        val result = matchesSimpleSchema(content)
        assertTrue(result)
    }
    @Test
    fun whenContentIncludesSchemaButExtends_ThenResultIsFalse() {
        val content = "{\"id\": 1, \"description\":\"abc\",\"another\": 3.14}"
        val result = matchesSimpleSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenContentDoesNotMatchSimpleSchema_ThenResultIsFalse() {
        val content = "{\"identity\": 1, \"desc\":\"abc\"}"
        val result = matchesSimpleSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenArrayOfSimpleTypeMatches_ThenResultIsTrue() {
        val content = "[{\"id\": 1, \"description\":\"abc\"},{\"id\": 2, \"description\":\"def\"},{\"id\": 3, \"description\":\"ghi\"}]"
        val result = matchesArrayOfSimpleSchema(content)
        assertTrue(result)
    }
    @Test
    fun whenArrayOfMixedTypes_ThenResultIsFalse() {
        val content = "[{\"id\": 1, \"description\":\"abc\"},{\"key1\":\"2\", \"key2\":3.14},{\"id\": 3, \"description\":\"ghi\"}]"
        val result = matchesArrayOfSimpleSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenContentContainsArrayButExtends_ThenResultIsFalse() {
        val content = "[{\"id\": 1, \"description\":\"abc\"},{\"key1\":\"2\", \"key2\":3.14},{\"id\": 3, \"description\":\"ghi\"}], {\"key1\":\"2\", \"key2\":3.14}"
        val result = matchesArrayOfSimpleSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenIsInvalidJsonArray_ThenResultIsFalse() {
        val content = "[{\"id\": 1, \"description\":\"abc\"},{{\"key1\":\"2\", \"key2\":3.14},{\"id\": 3, \"description\":\"ghi\"}]"
        val result = matchesArrayOfSimpleSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenIsObjectButExpectsArray_ThenResultIsFalse() {
        val content = "{\"id\": 1, \"description\":\"abc\"}"
        val result = matchesArrayOfSimpleSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenNestedObjectMatches_ThenResultIsTrue() {
        val content = "{\"list\": [{\"id\": 1, \"description\":\"abc\"},{\"id\": 2, \"description\":\"def\"},{\"id\": 3, \"description\":\"ghi\"}], \"nested\":{\"id\": 4, \"description\":\"jkl\"}}"
        val result = matchesNestedSchema(content)
        assertTrue(result)
    }
    @Test
    fun whenNestedObjectIncludesMixedTypesInArray_ThenResultIsFalse() {
        val content = "{\"list\": [{\"id\": 1, \"description\":\"abc\"},{\"key1\":\"2\", \"key2\":3.14},{\"id\": 3, \"description\":\"ghi\"}], \"nested\":{\"id\": 4, \"description\":\"jkl\"}}"
        val result = matchesNestedSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenNestedObjectIncludesSchemaButExtends_ThenResultIsFalse() {
        val content = "{\"list\": [{\"id\": 1, \"description\":\"abc\"},{\"id\": 2, \"description\":\"def\"},{\"id\": 3, \"description\":\"ghi\"}], \"nested\":{\"id\": 4, \"description\":\"jkl\"}, {\"arr\": [2,4,8,16,32]}}"
        val result = matchesNestedSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenNestedObjectContainsInvalidJson_ThenResultIsFalse() {
        val content = "{\"list\": [{\"id\": 1, \"description\":\"abc\"},{\"id\": 2, \"description\":\"def\"},{\"id\": 3, \"description\":\"ghi\"}], *\"nested\":{\"id\": 4, \"description\":\"jkl\"}}"
        val result = matchesNestedSchema(content)
        assertFalse(result)
    }
    @Test
    fun whenNestedObjectIsExpectedButEncountersAnotherType_ThenResultIsFalse() {
        val content = "[{\"id\": 1, \"description\":\"abc\"},{\"id\": 2, \"description\":\"def\"},{\"id\": 3, \"description\":\"ghi\"}]"
        val result = matchesNestedSchema(content)
        assertFalse(result)
    }
}
