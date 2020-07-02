package org.ionproject.integration.alert.interfaces

import org.ionproject.integration.utils.Try

interface AlertChannel {
    fun send(): Try<Boolean>
}
