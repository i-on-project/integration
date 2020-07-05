package org.ionproject.integration.model.internal.generic

data class Term(
    val name: String,
    val interruptions: List<Interruption>,
    val evaluations: List<Evaluation>,
    val details: List<TermDetail>,
    val otherEvents: List<OtherEvent>
)
