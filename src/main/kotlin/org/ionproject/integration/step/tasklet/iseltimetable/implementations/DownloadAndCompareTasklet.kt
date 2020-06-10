package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import java.net.URI
import java.nio.file.Path
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.step.tasklet.iseltimetable.exceptions.DownloadAndCompareTaskletException
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
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

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val pdfChecker = PDFBytesFormatChecker()
        val downloader = FileDownloaderImpl(pdfChecker)

        val file = localFileDestination.toFile()
        if (file.exists()) {
            throw DownloadAndCompareTaskletException("File already exists in $localFileDestination")
        }

        val path = downloader.download(pdfRemoteLocation, localFileDestination).orThrow()
        chunkContext.stepContext.stepExecution.jobExecution.executionContext.put(pdfKey, path)

        return RepeatStatus.FINISHED
    }
}
