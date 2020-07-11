package org.ionproject.integration.model.internal.generic

import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.external.generic.CoreExam
import org.ionproject.integration.model.external.generic.CoreExamEvent
import org.ionproject.integration.model.external.generic.CoreExamLabel
import org.ionproject.integration.model.external.generic.ICoreModel
import org.ionproject.integration.model.internal.timetable.School
import java.lang.IllegalArgumentException

data class ExamSchedule(
    val school: School,
    val programme: Programme,
    val academicYear: String,
    val exams: List<Exam>
) : IInternalModel {
    override fun toCore(): ICoreModel {
        val examEvents = examsToEvents(exams)
        return CoreExamSchedule(school, programme, academicYear,"pt-PT", examEvents)
    }

    private fun examsToEvents(exams: List<Exam>): List<CoreExam> {
        val examMap = exams.groupBy{ e -> e.name }
        return examMap.map { mapEntry -> toExamCore(mapEntry) }
    }

    private fun toExamCore(mapEntry: Map.Entry<String, List<Exam>>): CoreExam {
        val courseAcr = mapEntry.key
        val exams = mapEntry.value
        val label = CoreExamLabel(null, courseAcr)
        if(exams.size != 3){
            throw IllegalArgumentException("course $courseAcr has less than 3 dates")
        }
        return CoreExam(label, CoreExamEvent.fromInternalModelExam(exams))
    }
}
