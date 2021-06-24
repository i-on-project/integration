package org.ionproject.integration.infrastructure.notification

import org.ionproject.integration.infrastructure.Try

interface AlertChannel {
    fun send(): Try<Boolean>
}
