package org.ionproject.integration.model.internal.generic

import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.internal.timetable.School

data class ExamSchedule(
    val school: School,
    val programme: Programme,
    val academicYear: String,
    val exams: List<Exam>
) : IInternalModel {
    override fun toCore(): ICoreModel {
        return CoreExamSchedule()
    }
}
