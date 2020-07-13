package org.ionproject.integration.model.internal.generic

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.Date

@JsonInclude(JsonInclude.Include.NON_NULL)
class Exam(
    name: String,
    startDate: Date,
    endDate: Date,
    val location: String?
) : Event(name, startDate, endDate)
