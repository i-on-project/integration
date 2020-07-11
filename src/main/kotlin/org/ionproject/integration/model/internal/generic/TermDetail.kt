package org.ionproject.integration.model.internal.generic

import java.util.Date

class TermDetail(
    name: String,
    val curricularTerm: List<CurricularTerm>,
    startDate: Date,
    endDate: Date
) : Event(name, startDate, endDate)
