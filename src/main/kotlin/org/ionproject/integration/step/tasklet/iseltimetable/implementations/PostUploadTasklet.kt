package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import javax.sql.DataSource
import org.ionproject.integration.alert.implementations.IOnIntegrationEmailAlertService
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@Component("PostUploadTasklet")
@StepScope
class PostUploadTasklet() : Tasklet {
    private val log = LoggerFactory.getLogger(PostUploadTasklet::class.java)

    @Autowired
    private lateinit var ds: DataSource

    @Autowired
    private lateinit var sender: JavaMailSenderImpl

    @Value("#{jobParameters['jobId']}")
    private lateinit var jobId: String

    @Value("#{jobParameters['alertRecipient']}")
    private lateinit var alertRecipient: String

    @Value("#{jobParameters['pdfRemoteLocation']}")
    private lateinit var pdfRemoteLocation: String

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val hr = HashRepositoryImpl(ds)

        val fileHash = chunkContext.stepContext.stepExecution.jobExecution.executionContext.get("file-hash") as ByteArray
        hr.putHash(jobId, fileHash)

        sendEmail()

        return RepeatStatus.FINISHED
    }

    private fun sendEmail() {
        val asset = pdfRemoteLocation
            .substring(pdfRemoteLocation.lastIndexOf('/') + 1, pdfRemoteLocation.length)
        val alertService = IOnIntegrationEmailAlertService("ISEL Timetable Batch Job", alertRecipient, asset, sender)
        alertService.sendSuccessEmail()
        log.info("Email sent successfully")
    }
}
