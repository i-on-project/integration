package org.ionproject.integration.job

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.dispatcher.AcademicCalendarData
import org.ionproject.integration.dispatcher.IAcademicCalendarDispatcher
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.extractor.implementations.AcademicCalendarExtractor
import org.ionproject.integration.extractor.implementations.ITextPdfExtractor
import org.ionproject.integration.file.implementations.FileComparatorImpl
import org.ionproject.integration.file.implementations.FileDigestImpl
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.ionproject.integration.model.external.calendar.AcademicCalendar
import org.ionproject.integration.model.external.calendar.CalendarDto
import org.ionproject.integration.model.internal.calendar.isel.RawCalendarData
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.DownloadAndCompareTasklet
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Configuration
class ISELAcademicCalendarJob(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val properties: AppProperties,
    @Autowired
    val ds: DataSource
) {

    @Value("src/test/resources/calendarTest.pdf")
    lateinit var inputPdf: String
    lateinit var dispatcher: IAcademicCalendarDispatcher

    @Bean
    fun calendarJob() = jobBuilderFactory.get("ISEL Academic Calendar Batch Job")
        // .start(downloadCalendarPDFTasklet())
        // .on("STOPPED").end()
        // .next(extractCalendarPDFTasklet())
        .start(extractCalendarPDFTasklet())
        .next(createCalendarPDFBusinessObjectsTasklet())
        .next(createCalendarPDFDtoTasklet())
        // .next(writeCalendarDTOToGitTasklet())
        .next(sendNotificationsForCalendarJobTasklet())
        .build() // .build()

    @Bean
    fun downloadCalendarPDFTasklet() = stepBuilderFactory.get("Download Calendar PDF")
        .tasklet { stepContribution, chunkContext ->
            val pdfChecker = PDFBytesFormatChecker()
            val downloader = FileDownloaderImpl(pdfChecker, properties.timeoutInSeconds)
            val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
            DownloadAndCompareTasklet(downloader, fileComparator).execute(stepContribution, chunkContext)
        }
        .build()

    @Bean
    fun extractCalendarPDFTasklet() = stepBuilderFactory.get("Extract Calendar PDF Raw Data")
        .tasklet { _, _ ->
            State.rawCalendarData = extractCalendarPDF()
            RepeatStatus.FINISHED
        }
        .build()

    fun extractCalendarPDF(): RawCalendarData {
        try {
            val itext = ITextPdfExtractor()

            val headerText = itext.extract(inputPdf)
            val calendarTable = AcademicCalendarExtractor.calendarTable.extract(inputPdf)

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
