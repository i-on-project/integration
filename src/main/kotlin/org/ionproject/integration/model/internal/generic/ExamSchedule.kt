package org.ionproject.integration.model.internal.generic

import org.ionproject.integration.model.external.generic.CoreExam
import org.ionproject.integration.model.external.generic.CoreExamEvent
import org.ionproject.integration.model.external.generic.CoreExamLabel
import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.external.generic.ICoreModel

data class ExamSchedule(
    val school: School,
    val programme: Programme,
    val academicYear: String,
    val exams: List<Exam>
) : IInternalModel {
    override fun toCore(): ICoreModel {
        val examEvents = examsToCoreExams(exams)
        return CoreExamSchedule(school, programme, academicYear, "pt-PT", examEvents)
    }

    private fun examsToCoreExams(exams: List<Exam>): List<CoreExam> {
        val examMap = exams.groupBy { e -> e.name }
        return examMap.map { mapEntry -> toCoreExam(mapEntry) }
    }

    private fun toCoreExam(mapEntry: Map.Entry<String, List<Exam>>): CoreExam {
        val courseAcr = mapEntry.key
        val exams = mapEntry.value
        val label = CoreExamLabel(null, courseAcr)
        return CoreExam(label, CoreExamEvent.fromInternalModelExams(exams))
    }
}
