package org.ionproject.integration.application.job.tasklet

import org.ionproject.integration.application.JobEngine.Companion.JOB_HASH_PARAMETER
import org.ionproject.integration.application.JobEngine.Companion.REMOTE_FILE_LOCATION_PARAMETER
import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.infrastructure.IBytesFormatChecker
import org.ionproject.integration.infrastructure.IFileComparator
import org.ionproject.integration.infrastructure.IFileDownloader
import org.ionproject.integration.infrastructure.exceptions.FormatCheckException
import org.ionproject.integration.application.exception.DownloadAndCompareTaskletException
import org.slf4j.Logger
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
import org.springframework.stereotype.Component
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

@Component("DownloadAndCompareTasklet")
@StepScope
class DownloadAndCompareTasklet(
    private val downloader: IFileDownloader,
    private val formatChecker: IBytesFormatChecker,
    private val fileComparator: IFileComparator
) : Tasklet, StepExecutionListener {

    val log: Logger = LoggerFactory.getLogger(DownloadAndCompareTasklet::class.java)

    @Value("#{jobParameters['$REMOTE_FILE_LOCATION_PARAMETER']}")
    private lateinit var targetUri: URI

    @Value("#{jobParameters['$JOB_HASH_PARAMETER']}")
    private lateinit var jobId: String

    @Autowired
    private lateinit var appProperties: AppProperties

    private var fileIsEqualToLast: Boolean = false

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val fileName = parseFileName(targetUri)
        val outputDir = appProperties.tempFilesDir
        val localFileDestination: Path = Paths.get(outputDir.path, fileName)

        if (!outputDir.asFile.exists()) {
            outputDir.asFile.mkdirs()
        }

        val file = localFileDestination.toFile()
        if (file.isDirectory) {
            throw DownloadAndCompareTaskletException("Specified path $localFileDestination is a directory")
        }
        if (file.exists()) {
            throw DownloadAndCompareTaskletException("File already exists in $localFileDestination")
        }

        val path = downloader.download(targetUri, localFileDestination)
            .match(
                { it },
                {
                    file.delete()
                    throw it
                }
            )
        chunkContext.stepContext.stepExecution.jobExecution.executionContext.put("file-path", path)

        if (!formatChecker.isValidFormat(path.toFile().readBytes()))
            throw FormatCheckException("${path.fileName} is not a valid PDF file")

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
}
