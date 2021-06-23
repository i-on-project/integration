package org.ionproject.integration.application.job.chunkbased

import java.net.URI
import org.ionproject.integration.infrastructure.notification.EmailAlertChannel
import org.ionproject.integration.infrastructure.notification.EmailAlertService
import org.ionproject.integration.application.JobResult
import org.ionproject.integration.application.job.TIMETABLE_JOB_NAME
import org.ionproject.integration.infrastructure.EmailUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@StepScope
@Component("AlertOnFailureWriter")
class AlertOnFailureWriter() : ItemWriter<Try<Boolean>> {

    private val log = LoggerFactory.getLogger(AlertOnFailureWriter::class.java)

    @Autowired
    private lateinit var sender: JavaMailSenderImpl

    @Value("#{jobParameters['alertRecipient']}")
    private lateinit var alertRecipient: String

    @Value("#{jobParameters['srcRemoteLocation']}")
    private lateinit var srcRemoteLocation: URI

    override fun write(items: MutableList<out Try<Boolean>>) {
        val item = items.first()
        item.match(
            { r -> log.info("Result for Step 2 is $r") },
            { e ->
                log.info("Step 2 is about to throw exception $e with message ${e.message}")
                sendEmail(e)
            }
        )
        item.orThrow()
    }

    private fun sendEmail(e: Exception) {
        val filePath = srcRemoteLocation.toString()
        val asset = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length)

        val conf = EmailUtils.configure(
            TIMETABLE_JOB_NAME,
            JobResult.FAILED,
            alertRecipient,
            asset,
            e.message
        )
        val channel = EmailAlertChannel(conf, sender)
        val alertService = EmailAlertService(channel)
        alertService.sendEmail()
        log.info("Email sent successfully")
    }
}
