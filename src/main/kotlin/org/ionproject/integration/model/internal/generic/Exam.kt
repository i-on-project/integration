package org.ionproject.integration.model.internal.generic

import java.util.Date

data class Exam(
    val name: String,
    val date: Date,
    val location: String?
)
