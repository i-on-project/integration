package org.ionproject.integration.model.internal.generic

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.Date

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Exam(
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val location: String?
)
