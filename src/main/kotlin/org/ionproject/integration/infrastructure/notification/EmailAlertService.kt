package org.ionproject.integration.infrastructure.notification

import org.ionproject.integration.infrastructure.orThrow

class EmailAlertService(
    private val channel: EmailAlertChannel
) {

    fun sendEmail(): Boolean {
        return channel.send().orThrow()
    }
}
