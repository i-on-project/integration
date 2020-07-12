package org.ionproject.integration.model.external.generic

import org.ionproject.integration.model.internal.generic.CurricularTerm

data class CoreInterval(
    val startDate: String,
    val endDate: String,
    val name: String,
    val curricularTerm: List<CurricularTerm>?,
    val types: List<Int>?,
    val excludes: List<Int>?
)
