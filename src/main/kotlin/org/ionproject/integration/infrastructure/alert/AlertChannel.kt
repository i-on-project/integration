package org.ionproject.integration.infrastructure.alert

import org.ionproject.integration.utils.Try

interface AlertChannel {
    fun send(): Try<Boolean>
}
