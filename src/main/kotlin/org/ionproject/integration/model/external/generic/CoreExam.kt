package org.ionproject.integration.model.external.generic

data class CoreExam(
    val label: CoreExamLabel,
    val events: List<CoreExamEvent>
)
