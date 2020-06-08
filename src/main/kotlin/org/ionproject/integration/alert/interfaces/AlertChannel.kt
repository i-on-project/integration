package org.ionproject.integration.alert.interfaces

import org.ionproject.integration.utils.Try

interface AlertChannel {
    fun sendFailureAlert(e: Exception): Try<Boolean>
    fun sendSuccessAlert(): Try<Boolean>
}
