package org.ionproject.integration.model.external.timetable

import com.fasterxml.jackson.annotation.JsonInclude

data class TimetableDto(
    val creationDateTime: String,
    val retrievalDateTime: String,
    val school: SchoolDto,
    val programme: ProgrammeDto,
    val calendarTerm: String,
    val classes: List<ClassDto>
) {
    companion object {
        fun from(timetableTeachers: TimetableTeachers): TimetableDto {
            val creationDateTime = timetableTeachers.timetable[0].creationDateTime
            val retrievalDateTime = timetableTeachers.timetable[0].retrievalDateTime
            val school = SchoolDto(
                timetableTeachers.timetable[0].school.name,
                timetableTeachers.timetable[0].school.acr
            )
            val programme = ProgrammeDto(
                timetableTeachers.timetable[0].programme.name,
                timetableTeachers.timetable[0].programme.acr
            )
            val calendarTerm = timetableTeachers.timetable[0].calendarTerm

            val courseMap = HashMap<String, MutableList<SectionDto>>()

            // to get classes/courses associated to sections as per DTO definition
            for (timetable in timetableTeachers.timetable)
                for (course in timetable.courses) {

                    val tempEventList = mutableListOf<EventDto>()
                    for (event in course.events) {
                        for (weekday in event.weekday)
                            tempEventList.add(
                                EventDto(
                                    event.category.name,
                                    event.location.ifEmpty { null },
                                    event.beginTime,
                                    event.duration,
                                    weekday
                                )
                            )
                    }

                    val tempInstructorList = mutableListOf<InstructorDto>()

                    for (
                        faculty in timetableTeachers.teachers.filter { it.calendarSection == timetable.calendarSection }
                            .flatMap { it.courses }.filter { it.classDetail.acronym == course.label.acr }.toList()
                    ) {
                        for (instructor in faculty.instructors)
                            tempInstructorList.add(
                                InstructorDto(
                                    instructor.name,
                                    faculty.classDetail.type.name
                                )
                            )
                    }

                    if (courseMap[course.label.acr] != null)
                        courseMap[course.label.acr]?.add(
                            SectionDto(
                                timetable.calendarSection,
                                tempEventList,
                                tempInstructorList
                            )
                        )
                    else {
                        courseMap[course.label.acr] =
                            mutableListOf(SectionDto(timetable.calendarSection, tempEventList, tempInstructorList))
                    }
                }

            val classes = courseMap.toList().map { ClassDto(it.first, it.second) }

            return TimetableDto(
                creationDateTime,
                retrievalDateTime,
                school,
                programme,
                calendarTerm,
                classes
            )
        }
    }
}

data class SchoolDto(
    val name: String,
    val acr: String,
)

data class ProgrammeDto(
    val name: String,
    val acr: String,
)

data class ClassDto(
    val acr: String,
    val sections: List<SectionDto>
)

data class SectionDto(
    val section: String,
    val events: List<EventDto>,
    val instructors: List<InstructorDto>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EventDto(
    val category: String,
    val location: List<String>? = null,
    val beginTime: String, // Format HH:MM
    val duration: String, // Format HH:MM
    val weekdays: String
)

data class InstructorDto(
    val name: String,
    val category: String
)
