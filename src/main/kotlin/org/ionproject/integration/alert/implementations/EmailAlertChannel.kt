package org.ionproject.integration.alert.implementations

import org.ionproject.integration.alert.interfaces.AlertChannel
import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.utils.Try
import org.springframework.stereotype.Component

@Component
class EmailAlertChannel(val props: ISELTimetableProperties) : AlertChannel {
    override fun sendFailureAlert(e: Exception): Try<Boolean> {
        return Try.ofValue(true)
    }

    override fun sendSuccessAlert(): Try<Boolean> {
        return Try.ofValue(false)
    }
}
