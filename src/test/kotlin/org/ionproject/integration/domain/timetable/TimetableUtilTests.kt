package org.ionproject.integration.domain.timetable

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TimetableUtilTests {

    @Test
    fun `when parsing curricular terms given correct sections then success`() {
        val matches = mapOf(
            "LEIC11Da" to 1,
            "MI2N" to 2,
            "LI213N" to 2,
            "LMA21D" to 2,
            "LT31D" to 3,
            "LT41Da" to 4,
            "LT51D" to 5,
            "LI51N" to 5,
            "LT61N" to 6
        )

        Assertions.assertTrue(matches.all { (section, expectedTerm) -> getCurricularTermFromSection(section) == expectedTerm })
    }

    @Test
    fun `when parsing curricular terms given section without digits then failure`() {
        val invalidSection = "LEICxD"

        assertThrows<IllegalArgumentException> {
            getCurricularTermFromSection(invalidSection)
        }
    }

    @Test
    fun `when parsing curricular terms given section with only digits then failure`() {
        val invalidSection = "12334"

        assertThrows<IllegalArgumentException> {
            getCurricularTermFromSection(invalidSection)
        }
    }
}
