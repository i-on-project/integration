package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.step.tasklet.iseltimetable.exceptions.DownloadAndCompareTaskletException
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@Component(value = "DownloadAndCompareTasklet")
@StepScope
class DownloadAndCompareTasklet(val props: ISELTimetableProperties) : Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        val pdfChecker = PDFBytesFormatChecker()
        val downloader = FileDownloaderImpl(pdfChecker)

        val file = props.localFileDestination.toFile()
        if (file.exists()) {
            throw DownloadAndCompareTaskletException("File already exists in ${props.localFileDestination}")
        }

        val path = downloader.download(props.pdfRemoteLocation, props.localFileDestination).orThrow()
        chunkContext.stepContext.stepExecution.jobExecution.executionContext.put(props.pdfKey, path)

        return RepeatStatus.FINISHED
    }
}
