package org.ionproject.integration.dispatcher

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dto.CalendarTerm
import org.ionproject.integration.application.dto.InstitutionMetadata
import org.ionproject.integration.application.dto.ProgrammeMetadata
import org.ionproject.integration.application.dto.TimetableData
import org.ionproject.integration.domain.common.Term
import org.ionproject.integration.domain.common.dto.ProgrammeDto
import org.ionproject.integration.domain.common.dto.SchoolDto
import org.ionproject.integration.domain.timetable.dto.ClassDto
import org.ionproject.integration.domain.timetable.dto.EventDto
import org.ionproject.integration.domain.timetable.dto.InstructorDto
import org.ionproject.integration.domain.timetable.dto.SectionDto
import org.ionproject.integration.domain.timetable.dto.TimetableDto
import org.ionproject.integration.infrastructure.DateUtils
import org.ionproject.integration.infrastructure.file.FileWriter
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class FileWriterTests {

    @Autowired
    private lateinit var writer: FileWriter

    @Autowired
    internal lateinit var props: AppProperties

    val staging by lazy { props.stagingFilesDir }

    @Test
    fun `when given a timetable DTO then write the file to JSON`() {
        val file = writer.write(meta, OutputFormat.JSON, "test", staging)
        try {
            assert(file.exists())
            assertEquals(expectedJSON, file.readText())
        } finally {
            file.delete()
        }
    }

    @Test
    fun `when given a timetable DTO then write the file to YAML`() {
        val file = writer.write(meta, OutputFormat.YAML, "test", staging)

        try {
            assert(file.exists())
            assertEquals(expectedYAML, file.readText())
        } finally {
            file.delete()
        }
    }
}

private val expectedYAML =
    "---\ncreationDateTime: \"2021-04-21T20:49:16Z\"\nretrievalDateTime: \"2021-04-21T20:49:16Z\"\nschool:\n  name: \"INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA\"\n  acr: \"ISEL\"\nprogramme:\n  name: \"Licenciatura em Engenharia Informática e de Computadores\"\n  acr: \"LEIC\"\ncalendarTerm: \"2020-2021-2\"\nclasses:\n- acr: \"E\"\n  sections:\n  - section: \"LEIC11Da\"\n    curricularTerm: 1\n    events:\n    - category: \"LECTURE\"\n      location:\n      - \"L_H2\"\n      beginTime: \"14:00\"\n      duration: \"01:30\"\n      weekdays: \"MO\"\n    - category: \"LECTURE\"\n      beginTime: \"14:00\"\n      duration: \"01:30\"\n      weekdays: \"WE\"\n    - category: \"LECTURE\"\n      beginTime: \"14:00\"\n      duration: \"01:30\"\n      weekdays: \"TH\"\n    instructors:\n    - name: \"João Manuel Ferreira Martins\"\n      category: \"PRACTICE\"\n    - name: \"João Manuel Ferreira Martins\"\n      category: \"LECTURE\"\n"
private val expectedJSON =
    """{"creationDateTime":"2021-04-21T20:49:16Z","retrievalDateTime":"2021-04-21T20:49:16Z","school":{"name":"INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA","acr":"ISEL"},"programme":{"name":"Licenciatura em Engenharia Informática e de Computadores","acr":"LEIC"},"calendarTerm":"2020-2021-2","classes":[{"acr":"E","sections":[{"section":"LEIC11Da","curricularTerm":1,"events":[{"category":"LECTURE","location":["L_H2"],"beginTime":"14:00","duration":"01:30","weekdays":"MO"},{"category":"LECTURE","beginTime":"14:00","duration":"01:30","weekdays":"WE"},{"category":"LECTURE","beginTime":"14:00","duration":"01:30","weekdays":"TH"}],"instructors":[{"name":"João Manuel Ferreira Martins","category":"PRACTICE"},{"name":"João Manuel Ferreira Martins","category":"LECTURE"}]}]}]}"""

private val dateFormatted = LocalDateTime.of(
    2021,
    4,
    21,
    20,
    49,
    16,
).let { date ->
    DateUtils.formatToISO8601(date)
}

private val timetable = TimetableDto(
    dateFormatted,
    dateFormatted,
    SchoolDto(
        "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
        "ISEL"
    ),
    ProgrammeDto(
        "Licenciatura em Engenharia Informática e de Computadores",
        "LEIC"
    ),
    "2020-2021-2",
    listOf(
        ClassDto(
            "E",
            listOf(
                SectionDto(
                    "LEIC11Da",
                    1,
                    listOf(
                        EventDto(
                            "LECTURE",
                            listOf("L_H2"),
                            "14:00",
                            "01:30",
                            "MO"
                        ),
                        EventDto(
                            "LECTURE",
                            null,
                            "14:00",
                            "01:30",
                            "WE"
                        ),
                        EventDto(
                            "LECTURE",
                            null,
                            "14:00",
                            "01:30",
                            "TH"
                        )
                    ),
                    listOf(
                        InstructorDto(
                            "João Manuel Ferreira Martins",
                            "PRACTICE"
                        ),
                        InstructorDto(
                            "João Manuel Ferreira Martins",
                            "LECTURE"
                        )
                    )
                )
            )
        )
    )
)

private val institution = InstitutionMetadata("Instituto Superior de Engenharia de Lisboa", "ISEL", "pt.ipl.isel")
private val programmeMetadata = ProgrammeMetadata(institution, "Eng. Informática e de Computadores", "LEIC")
val meta = TimetableData(programmeMetadata, CalendarTerm(2020, Term.SPRING), timetable)
