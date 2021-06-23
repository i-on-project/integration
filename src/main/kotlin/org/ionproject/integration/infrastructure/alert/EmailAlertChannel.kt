package org.ionproject.integration.infrastructure.alert

import javax.mail.internet.InternetAddress
import org.ionproject.integration.infrastructure.exceptions.AlertChannelException
import org.ionproject.integration.utils.Try
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper

class EmailAlertChannel(
    private val conf: EmailConfiguration,
    private val emailSender: JavaMailSender
) : AlertChannel {

    private val log = LoggerFactory.getLogger(EmailAlertChannel::class.java)

    override fun send(): Try<Boolean> {
        val to: Array<InternetAddress> = conf.recipients
        val subject: String = conf.subject
        val text: String = conf.text

        val message = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(text, true)

        conf.attachments?.forEach { attachment ->
            helper.addAttachment(attachment.fileName, ByteArrayResource(attachment.byteArray))
        }

        return Try.of {
            log.info("About to send email to ${to.joinToString { addr -> addr.toUnicodeString() }} with subject $subject")
            emailSender.send(message)
            true
        }.mapError { _ -> AlertChannelException("E-mail alert could not be sent") }
    }
}
