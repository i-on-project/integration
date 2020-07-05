package org.ionproject.integration.model.internal.generic

data class TermDetail(

    val name: String,
    val curricularTerm: List<CurricularTerm>,
    val startDate: String,
    val endDate: String
)
