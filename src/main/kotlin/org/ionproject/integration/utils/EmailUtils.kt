package org.ionproject.integration.utils

import javax.mail.internet.InternetAddress
import org.ionproject.integration.alert.implementations.EmailConfiguration

const val EMAIL_HEADER = "i-on integration Alert - Job"

object EmailUtils {
    fun configure(
        jobName: String,
        result: JobResult,
        alertRecipient: String,
        asset: String,
        message: String?
    ): EmailConfiguration {
        val text =
            "$jobName ${result.name} for file: $asset" + if (result == JobResult.FAILED) " with message $message" else ""
        return EmailConfiguration(
            arrayOf(InternetAddress(alertRecipient)),
            null,
            "$EMAIL_HEADER ${result.name}",
            text
        )
    }
}
