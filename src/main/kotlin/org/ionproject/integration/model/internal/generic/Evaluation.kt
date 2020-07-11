package org.ionproject.integration.model.internal.generic

import java.util.Date

class Evaluation(
    name: String,
    startDate: Date,
    endDate: Date
) : Event(name, startDate, endDate)
