package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.alert.implementations.EmailAlertChannel
import org.ionproject.integration.alert.implementations.EmailAlertService
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.file.interfaces.IFileComparator
import org.ionproject.integration.file.interfaces.IFileDownloader
import org.ionproject.integration.model.internal.generic.JobType
import org.ionproject.integration.step.tasklet.iseltimetable.exceptions.DownloadAndCompareTaskletException
import org.ionproject.integration.utils.CompositeException
import org.ionproject.integration.utils.EmailUtils
import org.ionproject.integration.utils.JobResult
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

@Component("DownloadAndCompareTasklet")
@StepScope
class DownloadAndCompareTasklet(
    private val downloader: IFileDownloader,
    private val fileComparator: IFileComparator
) : Tasklet, StepExecutionListener {

    val log = LoggerFactory.getLogger(DownloadAndCompareTasklet::class.java)

    @Value("#{jobParameters['srcRemoteLocation']}")
    private lateinit var srcRemoteLocation: URI

    @Value("#{jobParameters['jobId']}")
    private lateinit var jobId: String

    @Value("#{jobParameters['alertRecipient']}")
    private lateinit var alertRecipient: String

    @Autowired
    private lateinit var appProperties: AppProperties

    @Autowired
    private lateinit var sender: JavaMailSenderImpl

    private var fileIsEqualToLast: Boolean = false

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val jobType = chunkContext.stepContext.jobParameters["jobType"] as String?
        val jobTypeEnum = if (jobType != null) enumValueOf<JobType>(jobType) else null
        val jobName = chunkContext.stepContext.jobName
        val fileName = parseFileName(srcRemoteLocation)
        val localFileDestination: Path = Paths.get(appProperties.resourcesFolder, fileName)

        val file = localFileDestination.toFile()
        if (file.isDirectory) {
            throw DownloadAndCompareTaskletException("Specified path $localFileDestination is a directory")
        }
        if (file.exists()) {
            throw DownloadAndCompareTaskletException("File already exists in $localFileDestination")
        }

        val path = downloader.download(srcRemoteLocation, localFileDestination, jobTypeEnum)
            .match(
                { it },
                {
                    log.error("Error during download step: $it -> Msg: ${it.message}")
                    file.delete()
                    throw it
                }
            )
        chunkContext.stepContext.stepExecution.jobExecution.executionContext.put("file-path", path)

        fileIsEqualToLast = fileComparator.compare(file, jobId)
            .match(
                {
                    if (it) {
                        path.toFile().delete()
                        log.warn("The job already ran successfully with this pdf file.")
                    }
                    it
                },
                {
                    path.toFile().delete()
                    throw it
                }
            )

        return RepeatStatus.FINISHED
    }

    override fun beforeStep(stepExecution: StepExecution) {
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        return if (fileIsEqualToLast)
            ExitStatus.STOPPED
        else ExitStatus.COMPLETED
    }

    private fun parseFileName(uri: URI): String {
        val path = uri.path
        return path.substring(path.lastIndexOf('/') + 1, path.length)
    }

    private fun selectMessageFromExceptionAndSendEmail(jobName: String, e: Exception, asset: String) {
        if (e is CompositeException) {
            val msg = e.exceptions[0].message
            sendEmail(jobName, msg!!, asset)
        } else {
            sendEmail(jobName, e.message!!, asset)
        }
        log.info("Email sent successfully")
    }

    private fun sendEmail(jobName: String, msg: String, asset: String) {
        val conf =
            EmailUtils.configure(
                jobName,
                JobResult.FAILED,
                alertRecipient,
                asset,
                msg
            )
        val channel = EmailAlertChannel(conf, sender)
        val alertService = EmailAlertService(channel)
        alertService.sendEmail()
    }
}
