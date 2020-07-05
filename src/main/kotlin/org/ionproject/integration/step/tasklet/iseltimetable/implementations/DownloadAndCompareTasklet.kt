package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import javax.sql.DataSource
import org.ionproject.integration.alert.implementations.EmailAlertService
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.file.interfaces.IFileComparator
import org.ionproject.integration.file.interfaces.IFileDownloader
import org.ionproject.integration.model.internal.generic.JobType
import org.ionproject.integration.step.tasklet.iseltimetable.exceptions.DownloadAndCompareTaskletException
import org.ionproject.integration.utils.CompositeException
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

    @Autowired
    private lateinit var ds: DataSource

    private var fileIsEqualToLast: Boolean = false

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val jobType = chunkContext.stepContext.jobParameters["jobType"] as String?
        val jobTypeEnum = if (jobType != null) enumValueOf<JobType>(jobType) else null
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
            .match({ it }, {
                file.delete()
                selectMessageFromExceptionAndSendEmail(it, fileName)
                throw it
            })
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
                    selectMessageFromExceptionAndSendEmail(it, fileName)
                    throw it
                })

        return RepeatStatus.FINISHED
    }

    override fun beforeStep(stepExecution: StepExecution) {
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        if (fileIsEqualToLast) {
            sendEmail("File Is equal to last successfully parsed", parseFileName(srcRemoteLocation))
            return ExitStatus.STOPPED
        }
        return ExitStatus.COMPLETED
    }

    private fun parseFileName(uri: URI): String {
        val path = uri.path
        return path.substring(path.lastIndexOf('/') + 1, path.length)
    }

    private fun selectMessageFromExceptionAndSendEmail(e: Exception, asset: String) {
        if (e is CompositeException) {
            val msg = e.exceptions[0].message
            sendEmail(msg!!, asset)
        } else {
            sendEmail(e.message!!, asset)
        }
        log.info("Email sent successfully")
    }

    private fun sendEmail(msg: String, asset: String) {
        val alertService = EmailAlertService("ISEL Timetable Batch Job", alertRecipient, asset, sender)
        alertService.sendFailureEmail(msg)
    }
}
