package org.ionproject.integration.infrastructure.alert

import org.ionproject.integration.utils.orThrow

class EmailAlertService(
    private val channel: EmailAlertChannel
) {

    fun sendEmail(): Boolean {
        return channel.send().orThrow()
    }
}
