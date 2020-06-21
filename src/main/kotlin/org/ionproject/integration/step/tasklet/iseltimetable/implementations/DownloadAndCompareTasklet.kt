package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.file.implementations.FileComparatorImpl
import org.ionproject.integration.file.implementations.FileDigestImpl
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.ionproject.integration.step.tasklet.iseltimetable.exceptions.DownloadAndCompareTaskletException
import org.ionproject.integration.utils.orThrow
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
import javax.sql.DataSource

@Component("DownloadAndCompareTasklet")
@StepScope
class DownloadAndCompareTasklet : Tasklet, StepExecutionListener {

    val log = LoggerFactory.getLogger(DownloadAndCompareTasklet::class.java)

    @Value("#{jobParameters['pdfRemoteLocation']}")
    private lateinit var pdfRemoteLocation: URI

    @Value("#{jobParameters['localFileDestination']}")
    private lateinit var localFileDestination: Path

    @Value("#{jobParameters['pdfKey']}")
    private lateinit var pdfKey: String

    @Value("#{jobParameters['jobId']}")
    private lateinit var jobId: String

    @Autowired
    private lateinit var ds: DataSource

    private var fileIsEqualToLast: Boolean = false
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val pdfChecker = PDFBytesFormatChecker()
        val downloader = FileDownloaderImpl(pdfChecker)
        val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))

        val file = localFileDestination.toFile()
        if (file.exists()) {
            throw DownloadAndCompareTaskletException("File already exists in $localFileDestination")
        }

        val path = downloader.download(pdfRemoteLocation, localFileDestination).orThrow()
        chunkContext.stepContext.stepExecution.jobExecution.executionContext.put(pdfKey, path)

        fileIsEqualToLast = fileComparator.compare(file, jobId)
            .match(
                {
                    if (it) {
                        path.toFile().deleteOnExit()
                        log.warn("The job already ran successfully with this pdf file.")
                    }
                    it
                },
                {
                    path.toFile().deleteOnExit()
                    throw it
                })

        return RepeatStatus.FINISHED
    }

    override fun beforeStep(stepExecution: StepExecution) {
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        if (fileIsEqualToLast) {
            return ExitStatus.STOPPED
        }
        return ExitStatus.COMPLETED
    }
}
