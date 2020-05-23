package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import java.io.File
import java.net.URI
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
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@Component(value = "DownloadAndCompareTasklet")
@StepScope
@PropertySource("isel-timetable.properties")
class DownloadAndCompareTasklet() : Tasklet {

    @Value("\${pdf-remote-location}")
    private lateinit var pdfLocation: String

    @Value("\${local-file-destination}")
    private lateinit var localFileDestination: String

    @Value("\${local-file-path-key}")
    private lateinit var pathKey: String

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        val pdfUri = Try.ofValue(URI(pdfLocation))
        val pdfChecker = PDFBytesFormatChecker()
        val downloader = FileDownloaderImpl(pdfChecker)

        val file = Try.ofValue(File(localFileDestination))
            .flatMap { f ->
                if (f.exists()) {
                    Try.ofError<DownloadAndCompareTaskletException>(DownloadAndCompareTaskletException("File already exists in $localFileDestination"))
                } else {
                    Try.ofValue(f)
                }
            }

        var exitStatus = Try.map(pdfUri, file) { uri, _ -> downloader.download(uri, localFileDestination) }
            .flatMap { it -> it }
            .map { path ->
                chunkContext.stepContext.stepExecution.jobExecution.executionContext.put(pathKey, path)
                RepeatStatus.FINISHED
            }
        return exitStatus.orThrow()
    }
}
