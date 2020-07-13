package org.ionproject.integration.model.internal.generic

import org.ionproject.integration.model.external.generic.CoreAcademicCalendar
import org.ionproject.integration.model.external.generic.CoreTerm
import org.ionproject.integration.model.external.generic.ICoreModel
import org.ionproject.integration.model.external.timetable.School
data class AcademicCalendar(
    val school: org.ionproject.integration.model.internal.generic.School,
    val terms: List<Term>
) : IInternalModel {
    override fun toCore(): ICoreModel {
        val school = School(school.name, school.acr)
        val coreTerms = terms.map { t -> CoreTerm.fromInternalTerm(t, school) }
        return CoreAcademicCalendar(coreTerms)
    }
}
