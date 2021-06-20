package org.ionproject.integration.dispatcher

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.model.external.timetable.ClassDto
import org.ionproject.integration.model.external.timetable.EventDto
import org.ionproject.integration.model.external.timetable.InstructorDto
import org.ionproject.integration.model.external.timetable.ProgrammeDto
import org.ionproject.integration.model.external.timetable.SchoolDto
import org.ionproject.integration.model.external.timetable.SectionDto
import org.ionproject.integration.model.external.timetable.TimetableDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FileWriterTests {

    @Autowired
    private lateinit var writer: FileWriter

    @Autowired
    internal lateinit var props: AppProperties

    val staging by lazy { props.stagingFilesDir }
    val repositoryName by lazy { props.gitRepository }

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
    "---\ncreationDateTime: \"20210421T204916Z\"\nretrievalDateTime: \"20210421T204916Z\"\nschool:\n  name: \"INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA\"\n  acr: \"ISEL\"\nprogramme:\n  name: \"Licenciatura em Engenharia Informática e de Computadores\"\n  acr: \"LEIC\"\ncalendarTerm: \"2020-2021-2\"\nclasses:\n- acr: \"E\"\n  sections:\n  - section: \"LEIC11Da\"\n    events:\n    - category: \"LECTURE\"\n      location:\n      - \"L_H2\"\n      beginTime: \"14:00\"\n      duration: \"01:30\"\n      weekdays: \"MO\"\n    - category: \"LECTURE\"\n      beginTime: \"14:00\"\n      duration: \"01:30\"\n      weekdays: \"WE\"\n    - category: \"LECTURE\"\n      beginTime: \"14:00\"\n      duration: \"01:30\"\n      weekdays: \"TH\"\n    instructors:\n    - name: \"João Manuel Ferreira Martins\"\n      category: \"PRACTICE\"\n    - name: \"João Manuel Ferreira Martins\"\n      category: \"LECTURE\"\n"
private val expectedJSON =
    "{\"creationDateTime\":\"20210421T204916Z\",\"retrievalDateTime\":\"20210421T204916Z\",\"school\":{\"name\":\"INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA\",\"acr\":\"ISEL\"},\"programme\":{\"name\":\"Licenciatura em Engenharia Informática e de Computadores\",\"acr\":\"LEIC\"},\"calendarTerm\":\"2020-2021-2\",\"classes\":[{\"acr\":\"E\",\"sections\":[{\"section\":\"LEIC11Da\",\"events\":[{\"category\":\"LECTURE\",\"location\":[\"L_H2\"],\"beginTime\":\"14:00\",\"duration\":\"01:30\",\"weekdays\":\"MO\"},{\"category\":\"LECTURE\",\"beginTime\":\"14:00\",\"duration\":\"01:30\",\"weekdays\":\"WE\"},{\"category\":\"LECTURE\",\"beginTime\":\"14:00\",\"duration\":\"01:30\",\"weekdays\":\"TH\"}],\"instructors\":[{\"name\":\"João Manuel Ferreira Martins\",\"category\":\"PRACTICE\"},{\"name\":\"João Manuel Ferreira Martins\",\"category\":\"LECTURE\"}]}]}]}"

private val timetable = TimetableDto(
    "20210421T204916Z",
    "20210421T204916Z",
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
