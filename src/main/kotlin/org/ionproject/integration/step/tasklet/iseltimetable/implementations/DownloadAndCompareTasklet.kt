package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.step.tasklet.iseltimetable.exceptions.DownloadAndCompareTaskletException
import org.ionproject.integration.utils.Try
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

        val pdfUri = Try.ofValue(props.pdfRemoteLocation)
        val pdfChecker = PDFBytesFormatChecker()
        val downloader = FileDownloaderImpl(pdfChecker)

        val file = Try.ofValue(props.localFileDestination.toFile())
            .flatMap { f ->
                if (f.exists()) {
                    Try.ofError<DownloadAndCompareTaskletException>(DownloadAndCompareTaskletException("File already exists in ${props.localFileDestination}"))
                } else {
                    Try.ofValue(f)
                }
            }

        var exitStatus = Try.map(pdfUri, file) { uri, _ -> downloader.download(uri, props.localFileDestination) }
            .flatMap { it -> it }
            .map { path ->
                chunkContext.stepContext.stepExecution.jobExecution.executionContext.put(props.pdfKey, path)
                RepeatStatus.FINISHED
            }
        return exitStatus.orThrow()
    }
}
