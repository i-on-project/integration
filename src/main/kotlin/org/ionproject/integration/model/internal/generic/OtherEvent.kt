package org.ionproject.integration.model.internal.generic

import java.util.Date

class OtherEvent(
    name: String,
    startDate: Date,
    endDate: Date
) : Event(name, startDate, endDate)
