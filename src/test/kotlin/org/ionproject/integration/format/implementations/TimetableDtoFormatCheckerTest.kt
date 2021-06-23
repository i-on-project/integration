package org.ionproject.integration.format.implementations

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ionproject.integration.domain.timetable.model.ClassDetail
import org.ionproject.integration.domain.timetable.dto.ClassDto
import org.ionproject.integration.domain.timetable.model.Course
import org.ionproject.integration.domain.timetable.model.CourseTeacher
import org.ionproject.integration.domain.timetable.model.EventCategory
import org.ionproject.integration.domain.timetable.dto.EventDto
import org.ionproject.integration.domain.timetable.model.Faculty
import org.ionproject.integration.domain.timetable.model.Instructor
import org.ionproject.integration.domain.timetable.dto.InstructorDto
import org.ionproject.integration.domain.timetable.model.Label
import org.ionproject.integration.domain.timetable.model.Programme
import org.ionproject.integration.domain.timetable.dto.ProgrammeDto
import org.ionproject.integration.domain.timetable.model.RecurrentEvent
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.timetable.dto.SchoolDto
import org.ionproject.integration.domain.timetable.dto.SectionDto
import org.ionproject.integration.domain.timetable.Timetable
import org.ionproject.integration.domain.timetable.dto.TimetableDto
import org.ionproject.integration.domain.timetable.TimetableTeachers
import org.ionproject.integration.domain.common.Weekday
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TimetableDtoFormatCheckerTest {

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
                                    EventCategory.LECTURE,
                                    listOf("L_H2"),
                                    "14:00",
                                    "01:30",
                                    listOf(Weekday.MONDAY)
                                ),
                                RecurrentEvent(
                                    null,
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf(Weekday.WEDNESDAY, Weekday.THURSDAY)
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
                                    EventCategory.PRACTICE,
                                    listOf("L_H2"),
                                    "14:00",
                                    "01:30",
                                    listOf(Weekday.MONDAY)
                                ),
                                RecurrentEvent(
                                    null,
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf(Weekday.WEDNESDAY, Weekday.THURSDAY)
                                )
                            )

                        ),
                        Course(
                            Label("LSD"),
                            listOf(
                                RecurrentEvent(
                                    null,
                                    EventCategory.PRACTICE,
                                    listOf("L_H1"),
                                    "15:30",
                                    "01:30",
                                    listOf(Weekday.MONDAY)
                                ),
                                RecurrentEvent(
                                    null,
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf(Weekday.TUESDAY)
                                ),
                                RecurrentEvent(
                                    null,
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "15:30",
                                    "01:30",
                                    listOf(Weekday.THURSDAY)
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
                                    EventCategory.PRACTICE,
                                    listOf("L_H2"),
                                    "15:30",
                                    "01:30",
                                    listOf(Weekday.MONDAY)
                                ),
                                RecurrentEvent(
                                    null,
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf(Weekday.WEDNESDAY, Weekday.THURSDAY)
                                )
                            )

                        ),
                        Course(
                            Label("LSD"),
                            listOf(
                                RecurrentEvent(
                                    null,
                                    EventCategory.PRACTICE,
                                    listOf("L_H1"),
                                    "17:00",
                                    "01:30",
                                    listOf(Weekday.MONDAY)
                                ),
                                RecurrentEvent(
                                    null,
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "14:00",
                                    "01:30",
                                    listOf(Weekday.TUESDAY)
                                ),
                                RecurrentEvent(
                                    null,
                                    EventCategory.LECTURE,
                                    listOf(),
                                    "15:30",
                                    "01:30",
                                    listOf(Weekday.THURSDAY)
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
