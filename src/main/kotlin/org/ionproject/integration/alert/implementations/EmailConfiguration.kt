package org.ionproject.integration.alert.implementations

import java.util.regex.Pattern
import javax.mail.internet.InternetAddress
import org.ionproject.integration.alert.exceptions.AlertConfigurationException
import org.ionproject.integration.model.internal.alert.email.Attachment

class EmailConfiguration private constructor(
    val recipients: Array<InternetAddress>,
    val attachments: Array<Attachment>?,
    val subject: String,
    val text: String
) {
    companion object {
        operator fun invoke(
            recipients: Array<InternetAddress>,
            attachments: Array<Attachment>?,
            subject: String,
            text: String
        ): EmailConfiguration {
            if (recipients.any { addr -> !isValidEmail(addr.address) }) {
                throw AlertConfigurationException("There is an invalid e-mail address in the list of recipients")
            } else return EmailConfiguration(recipients, attachments, subject, text)
        }

        private fun isValidEmail(email: String): Boolean {
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val inputStr: CharSequence = email
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(inputStr)
            return matcher.matches()
        }
    }
}
