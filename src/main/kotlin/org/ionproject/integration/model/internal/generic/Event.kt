package org.ionproject.integration.model.internal.generic

import java.util.Date

abstract class Event(
    val name: String,
    val startDate: Date,
    val endDate: Date
)
