package org.ionproject.integration.model.external.generic

import org.ionproject.integration.model.internal.generic.Programme
import org.ionproject.integration.model.internal.generic.School

class CoreExamSchedule(
    val school: School,
    val programme: Programme,
    val academicYear: String,
    val language: String,
    val exams: List<CoreExam>
) : ICoreModel
