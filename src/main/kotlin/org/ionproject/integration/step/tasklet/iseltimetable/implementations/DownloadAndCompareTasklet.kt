package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import java.net.URI
import java.nio.file.Path
import javax.sql.DataSource
import org.ionproject.integration.file.implementations.FileComparatorImpl
import org.ionproject.integration.file.implementations.FileDigestImpl
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.ionproject.integration.step.tasklet.iseltimetable.exceptions.DownloadAndCompareTaskletException
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component("DownloadAndCompareTasklet")
@StepScope
class DownloadAndCompareTasklet : Tasklet {

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

        val fileIsEqualToLast = fileComparator.compare(file, jobId).orThrow()

        if (fileIsEqualToLast) {
            path.toFile().deleteOnExit()
            throw DownloadAndCompareTaskletException("The job already ran successfully with this pdf file.")
        }
        return RepeatStatus.FINISHED
    }
}
