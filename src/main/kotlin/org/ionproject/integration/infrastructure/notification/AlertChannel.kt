package org.ionproject.integration.infrastructure.notification

import org.ionproject.integration.utils.Try

interface AlertChannel {
    fun send(): Try<Boolean>
}
