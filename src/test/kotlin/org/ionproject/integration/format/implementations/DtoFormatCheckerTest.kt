package org.ionproject.integration.format.implementations

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ionproject.integration.builder.implementations.ClassDetail
import org.ionproject.integration.model.external.timetable.ClassDto
import org.ionproject.integration.model.external.timetable.Course
import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.EventCategory
import org.ionproject.integration.model.external.timetable.EventDto
import org.ionproject.integration.model.external.timetable.Faculty
import org.ionproject.integration.model.external.timetable.Instructor
import org.ionproject.integration.model.external.timetable.InstructorDto
import org.ionproject.integration.model.external.timetable.Label
import org.ionproject.integration.model.external.timetable.Programme
import org.ionproject.integration.model.external.timetable.ProgrammeDto
import org.ionproject.integration.model.external.timetable.RecurrentEvent
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.external.timetable.SchoolDto
import org.ionproject.integration.model.external.timetable.SectionDto
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.external.timetable.TimetableDto
import org.ionproject.integration.model.external.timetable.TimetableTeachers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DtoFormatCheckerTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun `when Serialized Timetable is equal to expected Dto then Success`() {

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

        val json =
            """{"creationDateTime":"20210421T204916Z","retrievalDateTime":"20210421T204916Z","school":{"name":"INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA","acr":"ISEL"},"programme":{"name":"Licenciatura em Engenharia Informática e de Computadores","acr":"LEIC"},"calendarTerm":"2020-2021-2","classes":[{"acr":"E","sections":[{"section":"LEIC11Da","events":[{"category":"LECTURE","location":["L_H2"],"beginTime":"14:00","duration":"01:30","weekdays":"MO"},{"category":"LECTURE","beginTime":"14:00","duration":"01:30","weekdays":"WE"},{"category":"LECTURE","beginTime":"14:00","duration":"01:30","weekdays":"TH"}],"instructors":[{"name":"João Manuel Ferreira Martins","category":"PRACTICE"},{"name":"João Manuel Ferreira Martins","category":"LECTURE"}]}]}]}"""

        assertEquals(json, serialized)
    }

    @Test
    fun `when Simple Business object is equal to expected Dto then Success`() {
        val timetableTeachers = TimetableTeachers(
            listOf(
                Timetable(
                    "20210421T204916Z",
                    "20210421T204916Z",
                    School(
                        "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
                        "ISEL"
                    ),
                    Programme(
                        "Licenciatura em Engenharia Informática e de Computadores",
                        "LEIC"
                    ),
                    "2020-2021-2",
                    "LEIC11Da",
                    listOf(
                        Course(
                            Label("E"),
                            listOf(
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.LECTURE,
                                    listOf("L_H2"),
                                    "14:00",
                                    "01:30",
                                    listOf("MO")
                                ),
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf("WE", "TH")
                                )
                            )

                        )
                    )
                )
            ),
            listOf(
                CourseTeacher(
                    School(
                        "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
                        "ISEL"
                    ),
                    Programme(
                        "Licenciatura em Engenharia Informática e de Computadores",
                        "LEIC"
                    ),
                    "2020-2021-2",
                    "LEIC11Da",
                    listOf(
                        Faculty(
                            ClassDetail(
                                "E",
                                "L_H2",
                                EventCategory.PRACTICE
                            ),
                            listOf(
                                Instructor("João Manuel Ferreira Martins")
                            )
                        ),
                        Faculty(
                            ClassDetail(
                                "E",
                                "L_H2",
                                EventCategory.LECTURE
                            ),
                            listOf(
                                Instructor("João Manuel Ferreira Martins")
                            )
                        )
                    )
                )
            )
        )

        val timetableDto = TimetableDto(
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

        assertEquals(TimetableDto.from(timetableTeachers), timetableDto)
    }

    @Test
    fun `when Complex Business object is equal to expected Dto then Success`() {
        val timetableTeachers = TimetableTeachers(
            listOf(
                Timetable(
                    "20210421T204916Z",
                    "20210421T204916Z",
                    School(
                        "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
                        "ISEL"
                    ),
                    Programme(
                        "Licenciatura em Engenharia Informática e de Computadores",
                        "LEIC"
                    ),
                    "2020-2021-2",
                    "LEIC11Da",
                    listOf(
                        Course(
                            Label("E"),
                            listOf(
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.PRACTICE,
                                    listOf("L_H2"),
                                    "14:00",
                                    "01:30",
                                    listOf("MO")
                                ),
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf("WE", "TH")
                                )
                            )

                        ),
                        Course(
                            Label("LSD"),
                            listOf(
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.PRACTICE,
                                    listOf("L_H1"),
                                    "15:30",
                                    "01:30",
                                    listOf("MO")
                                ),
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf("TU")
                                ),
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "15:30",
                                    "01:30",
                                    listOf("TH")
                                )
                            )
                        )
                    )
                ),
                Timetable(
                    "20210421T204916Z",
                    "20210421T204916Z",
                    School(
                        "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
                        "ISEL"
                    ),
                    Programme(
                        "Licenciatura em Engenharia Informática e de Computadores",
                        "LEIC"
                    ),
                    "2020-2021-2",
                    "LEIC11Db",
                    listOf(
                        Course(
                            Label("E"),
                            listOf(
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.PRACTICE,
                                    listOf("L_H2"),
                                    "15:30",
                                    "01:30",
                                    listOf("MO")
                                ),
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf("WE", "TH")
                                )
                            )

                        ),
                        Course(
                            Label("LSD"),
                            listOf(
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.PRACTICE,
                                    listOf("L_H1"),
                                    "17:00",
                                    "01:30",
                                    listOf("MO")
                                ),
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf("TU")
                                ),
                                RecurrentEvent(
                                    null,
                                    "",
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "15:30",
                                    "01:30",
                                    listOf("TH")
                                )
                            )
                        )
                    )
                )
            ),
            listOf(
                CourseTeacher(
                    School(
                        "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
                        "ISEL"
                    ),
                    Programme(
                        "Licenciatura em Engenharia Informática e de Computadores",
                        "LEIC"
                    ),
                    "2020-2021-2",
                    "LEIC11Da",
                    listOf(
                        Faculty(
                            ClassDetail(
                                "E",
                                "L_H2",
                                EventCategory.PRACTICE
                            ),
                            listOf(
                                Instructor("João Manuel Ferreira Martins")
                            )
                        ),
                        Faculty(
                            ClassDetail(
                                "E",
                                "L_H2", // TODO redundant in having the location on ClassDetail IMO
                                EventCategory.LECTURE
                            ),
                            listOf(
                                Instructor("João Manuel Ferreira Martins")
                            )
                        ),
                        Faculty(
                            ClassDetail(
                                "LSD",
                                "L_H1",
                                EventCategory.PRACTICE
                            ),
                            listOf(
                                Instructor("José Manuel Bagarrão Paraíso")
                            )
                        ),
                        Faculty(
                            ClassDetail(
                                "LSD",
                                "",
                                EventCategory.LECTURE
                            ),
                            listOf(
                                Instructor("José Manuel Bagarrão Paraíso")
                            )
                        )
                    )
                ),
                CourseTeacher(
                    School(
                        "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
                        "ISEL"
                    ),
                    Programme(
                        "Licenciatura em Engenharia Informática e de Computadores",
                        "LEIC"
                    ),
                    "2020-2021-2",
                    "LEIC11Db",
                    listOf(
                        Faculty(
                            ClassDetail(
                                "E",
                                "L_H2",
                                EventCategory.PRACTICE
                            ),
                            listOf(
                                Instructor("João Manuel Ferreira Martins")
                            )
                        ),
                        Faculty(
                            ClassDetail(
                                "E",
                                "L_H2", // TODO redundant in having the location on ClassDetail IMO
                                EventCategory.LECTURE
                            ),
                            listOf(
                                Instructor("João Manuel Ferreira Martins")
                            )
                        ),
                        Faculty(
                            ClassDetail(
                                "LSD",
                                "L_H1",
                                EventCategory.PRACTICE
                            ),
                            listOf(
                                Instructor("José Manuel Bagarrão Paraíso")
                            )
                        ),
                        Faculty(
                            ClassDetail(
                                "LSD",
                                "",
                                EventCategory.LECTURE
                            ),
                            listOf(
                                Instructor("José Manuel Bagarrão Paraíso")
                            )
                        )
                    )
                )
            )
        )

        val timetableDto = TimetableDto(
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
                                    "PRACTICE",
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
                        ),
                        SectionDto(
                            "LEIC11Db",
                            listOf(
                                EventDto(
                                    "PRACTICE",
                                    listOf("L_H2"),
                                    "15:30",
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
                ),
                ClassDto(
                    "LSD",
                    listOf(
                        SectionDto(
                            "LEIC11Da",
                            listOf(
                                EventDto(
                                    "PRACTICE",
                                    listOf("L_H1"),
                                    "15:30",
                                    "01:30",
                                    "MO"
                                ),
                                EventDto(
                                    "LECTURE",
                                    null,
                                    "14:00",
                                    "01:30",
                                    "TU"
                                ),
                                EventDto(
                                    "LECTURE",
                                    null,
                                    "15:30",
                                    "01:30",
                                    "TH"
                                )
                            ),
                            listOf(
                                InstructorDto(
                                    "José Manuel Bagarrão Paraíso",
                                    "PRACTICE"
                                ),
                                InstructorDto(
                                    "José Manuel Bagarrão Paraíso",
                                    "LECTURE"
                                )
                            )
                        ),
                        SectionDto(
                            "LEIC11Db",
                            listOf(
                                EventDto(
                                    "PRACTICE",
                                    listOf("L_H1"),
                                    "17:00",
                                    "01:30",
                                    "MO"
                                ),
                                EventDto(
                                    "LECTURE",
                                    null,
                                    "14:00",
                                    "01:30",
                                    "TU"
                                ),
                                EventDto(
                                    "LECTURE",
                                    null,
                                    "15:30",
                                    "01:30",
                                    "TH"
                                )
                            ),
                            listOf(
                                InstructorDto(
                                    "José Manuel Bagarrão Paraíso",
                                    "PRACTICE"
                                ),
                                InstructorDto(
                                    "José Manuel Bagarrão Paraíso",
                                    "LECTURE"
                                )
                            )
                        )
                    )
                )
            )
        )

        assertEquals(TimetableDto.from(timetableTeachers), timetableDto)
    }
}
