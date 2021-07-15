package org.ionproject.integration.domain.timetable.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.ionproject.integration.domain.common.dto.ProgrammeDto
import org.ionproject.integration.domain.common.dto.SchoolDto
import org.ionproject.integration.domain.timetable.TimetableTeachers
import org.ionproject.integration.domain.timetable.getCurricularTermFromSection
import org.ionproject.integration.domain.timetable.model.Course
import org.ionproject.integration.domain.timetable.model.CourseTeacher
import org.ionproject.integration.domain.timetable.model.EventCategory
import org.ionproject.integration.domain.timetable.model.Instructor
import org.ionproject.integration.domain.timetable.model.RecurrentEvent
import org.ionproject.integration.infrastructure.DateUtils

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

            with(timetableTeachers) {
                val creationDateTime = DateUtils.formatToISO8601(timetable[0].creationDateTime)
                val retrievalDateTime = DateUtils.formatToISO8601(timetable[0].retrievalDateTime)
                val school = SchoolDto(
                    timetable[0].school.name,
                    timetable[0].school.acr
                )
                val programme = ProgrammeDto(
                    timetable[0].programme.name,
                    timetable[0].programme.acr
                )
                val calendarTerm = timetable[0].calendarTerm

                // to get classes/courses associated to sections as per DTO definition
                val classes = timetable.flatMap { timetable ->
                    timetable.courses.map { course ->
                        getAcronymAndSectionDto(course, timetableTeachers, timetable.calendarSection)
                    }
                }
                    .groupBy(keySelector = { it.first }, valueTransform = { it.second })
                    .map { (acronym, sections) ->
                        val normalizedSections = sections.normalized()
                        ClassDto(acronym, normalizedSections)
                    }

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

        private fun getAcronymAndSectionDto(
            course: Course,
            timetableTeachers: TimetableTeachers,
            calendarSection: String
        ): Pair<String, SectionDto> {
            val events = course.events.flatMap(EventDto.Factory::from)
            val instructors = getInstructors(timetableTeachers.teachers, course, calendarSection)

            val acronym = course.label.acr
            val section = SectionDto(
                calendarSection,
                getCurricularTermFromSection(calendarSection),
                events,
                instructors
            )

            return acronym to section
        }

        private fun getInstructors(
            courseTeacherList: List<CourseTeacher>,
            course: Course,
            section: String
        ): List<InstructorDto> {
            return courseTeacherList
                .filter { it.calendarSection == section }
                .flatMap { it.courses }
                .filter { it.classDetail.acronym == course.label.acr }
                .flatMap { faculty ->
                    faculty.instructors.map {
                        InstructorDto.from(it, faculty.classDetail.type)
                    }
                }
        }
    }
}

private fun List<SectionDto>.normalized(): List<SectionDto> {
    return groupBy { it.section }.values.map { sections ->
        val sample = sections.first() // All fields will be the same except the events
        SectionDto(
            section = sample.section,
            curricularTerm = sample.curricularTerm,
            instructors = sample.instructors,
            events = sections.flatMap { section -> section.events }
        )
    }
}

data class ClassDto(
    val acr: String,
    val sections: List<SectionDto>
)

data class SectionDto(
    val section: String,
    val curricularTerm: Int,
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
) {
    companion object Factory {
        fun from(event: RecurrentEvent): List<EventDto> = event.weekdays.map { weekday ->
            EventDto(
                event.category.name,
                event.location.ifEmpty { null },
                event.beginTime,
                event.duration,
                weekday.shortName
            )
        }
    }
}

data class InstructorDto(
    val name: String,
    val category: String
) {
    companion object Factory {
        fun from(instructor: Instructor, eventCategory: EventCategory): InstructorDto {
            return InstructorDto(instructor.name, eventCategory.name)
        }
    }
}
