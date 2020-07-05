package org.ionproject.integration.model.internal.generic

import org.ionproject.integration.model.internal.timetable.School

data class AcademicCalendar(

    val school: School,
    val academicYear: String,
    val terms: List<Term>
) : IInternalModel {
    override fun toCore(): ICoreModel {
        TODO("Not yet implemented")
    }
}
