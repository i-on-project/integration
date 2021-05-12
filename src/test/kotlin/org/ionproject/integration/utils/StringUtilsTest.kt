package org.ionproject.integration.utils

import org.ionproject.integration.model.external.timetable.Language
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
}

private fun assertAcronymEquals(origin: String, acronym: String, toIgnore: List<String> = emptyList()) =
    assertEquals(acronym, generateAcronym(origin, toIgnore))
