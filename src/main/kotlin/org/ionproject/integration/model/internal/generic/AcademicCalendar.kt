package org.ionproject.integration.model.internal.generic

import org.ionproject.integration.model.external.generic.CoreAcademicCalendar
import org.ionproject.integration.model.external.generic.ICoreModel

data class AcademicCalendar(
    val school: School,
    val academicYear: String,
    val terms: List<Term>
) : IInternalModel {
    override fun toCore(): ICoreModel {
        return CoreAcademicCalendar()
    }
}
