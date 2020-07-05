package org.ionproject.integration.model.internal.generic

import java.util.Date

data class Event(
    val name: String,
    val startDate: Date,
    val endDate: Date
)
