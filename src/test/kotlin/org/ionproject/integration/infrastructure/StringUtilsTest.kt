package org.ionproject.integration.infrastructure

import org.ionproject.integration.domain.common.Language
import org.ionproject.integration.infrastructure.text.IgnoredWords
import org.ionproject.integration.infrastructure.text.generateAcronym
import org.ionproject.integration.infrastructure.text.removeDuplicateSpaces
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringUtilsTest {

    @Test
    fun `when capitalized words then success`() = assertAcronymEquals(
        "Portable Network Graphics",
        "PNG"
    )

    @Test
    fun `when proper case words then success`() = assertAcronymEquals(
        "Instituto Superior Técnico",
        "IST"
    )

    @Test
    fun `when lower case words then success`() = assertAcronymEquals(
        "dirty rotten imbeciles",
        "DRI"
    )

    @Test
    fun `when text has punctuation then it's ignored`() = assertAcronymEquals(
        "First In, First Out",
        "FIFO"
    )

    @Test
    fun `when uppercased word at the start then success`() = assertAcronymEquals(
        "GNU Image Manipulation Program",
        "GIMP"
    )

    @Test
    fun `when text has hyphen then it's counted as two words`() = assertAcronymEquals(
        "Complementary metal-oxide semiconductor",
        "CMOS"
    )

    @Test
    fun `when text is blank then result is empty string`() = assertAcronymEquals(
        "          ",
        ""
    )

    @Test
    fun `when text is blank and delimiters only then result is empty string`() = assertAcronymEquals(
        " -_, ",
        ""
    )

    @Test
    fun `when having consecutive delimiters then success`() = assertAcronymEquals(
        """This    text-has multiple 
            |delimiters,,,-ok's""".trimMargin(),
        "TTHMDO"
    )

    @Test
    fun `when text has an apostrophe then it is ignored`() = assertAcronymEquals(
        "Halley's Comet",
        "HC"
    )

    @Test
    fun `when text has underscore then it is ignored`() = assertAcronymEquals(
        "Old _Town_ Road",
        "OTR"
    )

    @Test
    fun `when text has ignored portuguese words then acronym cannot have them`() = assertAcronymEquals(
        "Instituto Superior de Engenharia de Lisboa",
        "ISEL",
        IgnoredWords.of(Language.PT)
    )

    @Test
    fun `when text has more ignored portuguese words then success`() {
        assertAcronymEquals(
            "Mestrado em Engenharia Informática e Multimédia",
            "MEIM",
            IgnoredWords.of(Language.PT)

        )
    }

    @Test
    fun `when text has ignored portuguese words and various delimiters then success`() = assertAcronymEquals(
        "Super-sigla de Teste para Coisas",
        "SSTC",
        IgnoredWords.of(Language.PT)
    )

    @Test
    fun `when text has ignored english words then success`() = assertAcronymEquals(
        "Massachusetts Institute of Technology",
        "MIT",
        IgnoredWords.of(Language.EN_US)
    )

    @Test
    fun `when text has ignored english words and delimiters then success`() = assertAcronymEquals(
        "Carnegie-Mellon University",
        "CMU",
        IgnoredWords.of(Language.EN_US)
    )

    @Test
    fun `when text has ignored portuguese words in a different case then success`() = assertAcronymEquals(
        "Escola Superior de Saúde DR. Lopes Dias",
        "ESSLD",
        IgnoredWords.of(Language.PT)
    )

    @Test
    fun `given a string when having 2 consecutive spaces then replace it`() {
        val expected = "21 a 22"
        val actual = "21  a 22".removeDuplicateSpaces()

        assertEquals(expected, actual)
    }

    @Test
    fun `given a string when having more than 2 consecutive spaces then replace it`() {
        val expected = "miami disco"
        val actual = "miami     disco".removeDuplicateSpaces()

        assertEquals(expected, actual)
    }

    @Test
    fun `given a string when having multiple consecutive spaces then replace all`() {
        val expected = "humans are such easy prey"
        val actual = "humans  are   such    easy     prey".removeDuplicateSpaces()

        assertEquals(expected, actual)
    }

    @Test
    fun `given a string without consecutive spaces then return unchanged`() {
        val expected = "extreme weather events !"
        val actual = "extreme weather events !".removeDuplicateSpaces()

        assertEquals(expected, actual)
    }

    @Test
    fun `given a string with consecutive newlines then replace with one newline`() {
        val expected = "extreme\nevents !"
        val actual = "extreme\n\nevents !".removeDuplicateSpaces()

        assertEquals(expected, actual)
    }

    @Test
    fun `given a string with consecutive newlines and whitespace then replace with all`() {
        val expected = "cowboys\nfrom hell !"
        val actual = "cowboys\n\nfrom   hell  !".removeDuplicateSpaces()

        assertEquals(expected, actual)
    }
}

private fun assertAcronymEquals(origin: String, acronym: String, toIgnore: List<String> = emptyList()) =
    assertEquals(acronym, generateAcronym(origin, toIgnore))
