package org.ionproject.integration.job

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.dispatcher.AcademicCalendarData
import org.ionproject.integration.dispatcher.IAcademicCalendarDispatcher
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.extractor.implementations.AcademicCalendarExtractor
import org.ionproject.integration.extractor.implementations.ITextPdfExtractor
import org.ionproject.integration.file.implementations.FileComparatorImpl
import org.ionproject.integration.file.implementations.FileDigestImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.file.interfaces.IFileDownloader
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.ionproject.integration.model.external.calendar.AcademicCalendar
import org.ionproject.integration.model.external.calendar.CalendarDto
import org.ionproject.integration.model.internal.calendar.isel.RawCalendarData
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.DownloadAndCompareTasklet
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Configuration
class ISELAcademicCalendarJob(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val properties: AppProperties,
    val downloader: IFileDownloader,
    val dispatcher: IAcademicCalendarDispatcher,
    @Autowired
    val ds: DataSource
) {

    @Bean
    fun calendarJob() = jobBuilderFactory.get("ISEL Academic Calendar Batch Job")
        .start(taskletStep("Download And Compare", downloadCalendarPDFAlternateTasklet()))
        .on("STOPPED").end()
        .next(extractCalendarPDFTasklet())
        .next(createCalendarPDFBusinessObjectsTasklet())
        .next(createCalendarPDFDtoTasklet())
        .next(writeCalendarDTOToGitTasklet())
        .next(sendNotificationsForCalendarJobTasklet())
        .build().build()

    private fun taskletStep(name: String, tasklet: Tasklet): TaskletStep {
        return stepBuilderFactory
            .get(name)
            .tasklet(tasklet)
            .build()
    }

    @StepScope
    @Bean
    fun downloadCalendarPDFAlternateTasklet(): DownloadAndCompareTasklet {
        val pdfChecker = PDFBytesFormatChecker()
        val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        return DownloadAndCompareTasklet(downloader, pdfChecker, fileComparator)
    }

    @Bean
    fun downloadCalendarPDFTasklet() = stepBuilderFactory.get("Download Calendar PDF")
        .tasklet { stepContribution, chunkContext ->
            val pdfChecker = PDFBytesFormatChecker()
            val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
            DownloadAndCompareTasklet(downloader, pdfChecker, fileComparator).execute(stepContribution, chunkContext)
        }
        .build()

    @Bean
    fun extractCalendarPDFTasklet() = stepBuilderFactory.get("Extract Calendar PDF Raw Data")
        .tasklet { stepContribution, _ ->
            val path = stepContribution.stepExecution.jobExecution.executionContext.get("file-path").toString()

            State.rawCalendarData = extractCalendarPDF(path)
            RepeatStatus.FINISHED
        }
        .build()

    fun extractCalendarPDF(path: String): RawCalendarData {
        try {
            val itext = ITextPdfExtractor()

            val headerText = itext.extract(path)
            val calendarTable = AcademicCalendarExtractor.calendarTable.extract(path)

            return Try.map(
                headerText,
                calendarTable
            ) { (text, calendarTable) ->
                RawCalendarData(
                    text.dropLast(1),
                    calendarTable.first().replace("\\r", " "),
                    text.last()
                )
            }.orThrow()
        } finally {
        }
    }

    @Bean
    fun createCalendarPDFBusinessObjectsTasklet() =
        stepBuilderFactory.get("Create Business Objects from Calendar Raw Data")
            .tasklet { _, _ ->
                State.academicCalendar = AcademicCalendar.from(State.rawCalendarData)
                RepeatStatus.FINISHED
            }
            .build()

    @Bean
    fun createCalendarPDFDtoTasklet() = stepBuilderFactory.get("Create DTO from Calendar Business Objects")
        .tasklet { _, _ ->
            State.academicCalendarDto = CalendarDto.from(State.academicCalendar)
            RepeatStatus.FINISHED
        }
        .build()

    @Bean
    fun writeCalendarDTOToGitTasklet() = stepBuilderFactory.get("Write Calendar DTO to Git")
        .tasklet { _, _ ->
            dispatcher.dispatch(AcademicCalendarData.from(State.academicCalendarDto), OutputFormat.JSON)
            RepeatStatus.FINISHED
        }
        .build()

    @Bean
    fun sendNotificationsForCalendarJobTasklet() = stepBuilderFactory.get("Send Calendar Job Notifications")
        .tasklet { _, _ ->
            // TODO
            RepeatStatus.FINISHED
        }
        .build()

    @Component
    object State {
        lateinit var rawCalendarData: RawCalendarData
        lateinit var academicCalendar: AcademicCalendar
        lateinit var academicCalendarDto: CalendarDto
    }
}
