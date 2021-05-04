package org.ionproject.integration.format.implementations

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ionproject.integration.model.external.timetable.ClassDto
import org.ionproject.integration.model.external.timetable.EventDto
import org.ionproject.integration.model.external.timetable.InstructorDto
import org.ionproject.integration.model.external.timetable.ProgrammeDto
import org.ionproject.integration.model.external.timetable.SchoolDto
import org.ionproject.integration.model.external.timetable.SectionDto
import org.ionproject.integration.model.external.timetable.TimetableDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DtoFormatCheckerTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun whenSerializeTimetableDto_theSuccess() {

        val timetable = TimetableDto(
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
        val serialized = mapper.writeValueAsString(timetable)

        val json = """{"creationDateTime":"20210421T204916Z","retrievalDateTime":"20210421T204916Z","school":{"name":"INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA","acr":"ISEL"},"programme":{"name":"Licenciatura em Engenharia Informática e de Computadores","acr":"LEIC"},"calendarTerm":"2020-2021-2","classes":[{"acr":"E","sections":[{"section":"LEIC11Da","events":[{"category":"LECTURE","location":["L_H2"],"beginTime":"14:00","duration":"01:30","weekdays":"MO"},{"category":"LECTURE","beginTime":"14:00","duration":"01:30","weekdays":"WE"},{"category":"LECTURE","beginTime":"14:00","duration":"01:30","weekdays":"TH"}],"instructors":[{"name":"João Manuel Ferreira Martins","category":"PRACTICE"},{"name":"João Manuel Ferreira Martins","category":"LECTURE"}]}]}]}"""

        assertEquals(json, serialized)
    }
}
