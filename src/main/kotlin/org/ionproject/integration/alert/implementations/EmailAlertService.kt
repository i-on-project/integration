package org.ionproject.integration.alert.implementations

import javax.mail.internet.InternetAddress
import org.ionproject.integration.utils.orThrow
import org.springframework.mail.javamail.JavaMailSenderImpl

const val EMAIL_HEADER = "i-on integration Alert - Job"

class EmailAlertService(
    private val jobName: String,
    private val alertRecipient: String,
    private val asset: String,
    private val sender: JavaMailSenderImpl
) {

    fun sendSuccessEmail(): Boolean {
        val conf = EmailConfiguration(
            arrayOf(InternetAddress(alertRecipient)),
            null,
            "$EMAIL_HEADER Completed Successfully",
            "$jobName successfully completed for file: $asset"
        )
        val channel = EmailAlertChannel(conf, sender)
        return channel.send().orThrow()
    }

    fun sendFailureEmail(message: String): Boolean {
        val conf = EmailConfiguration(
            arrayOf(InternetAddress(alertRecipient)),
            null,
            "$EMAIL_HEADER Failed",
            "$jobName failed with message: $message " +
                "for file $asset"
        )
        val channel = EmailAlertChannel(conf, sender)
        return channel.send().orThrow()
    }
}
