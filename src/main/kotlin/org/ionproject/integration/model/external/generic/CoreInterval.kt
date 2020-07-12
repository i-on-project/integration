package org.ionproject.integration.model.external.generic

data class CoreInterval(
    val startDate: String,
    val endDate: String,
    val name: String,
    val curricularTerm: List<Int>?,
    val categories: List<Int>?,
    val excludes: List<Int>?
)
