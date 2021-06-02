package org.ionproject.integration.job

import org.ionproject.integration.extractor.implementations.AcademicCalendarExtractor
import org.ionproject.integration.extractor.implementations.ITextPdfExtractor
import org.ionproject.integration.file.implementations.FileComparatorImpl
import org.ionproject.integration.file.implementations.FileDigestImpl
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
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
    @Autowired
    val ds: DataSource
) {

    @Value("src/test/resources/calendarTest.pdf")
    lateinit var inputPdf: String

    @Bean
    fun calendarJob() = jobBuilderFactory.get("ISEL Academic Calendar Batch Job")
        // .start(downloadCalendarPDFTasklet())
        // .on("STOPPED").end()
        // .next(extractCalendarPDFTasklet())
        .start(extractCalendarPDFTasklet())
        .next(createCalendarPDFDTOTasklet())
        .next(sendNotificationsForCalendarJobTasklet())
        .build() // .build()

    @Bean
    fun downloadCalendarPDFTasklet() = stepBuilderFactory.get("Download Calendar PDF")
        .tasklet { stepContribution, chunkContext ->
            val pdfChecker = PDFBytesFormatChecker()
            val downloader = FileDownloaderImpl(pdfChecker)
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
            val fd = FileDigestImpl()

            val headerText = itext.extract(inputPdf)
            val winterSemester = AcademicCalendarExtractor.winterSemester.extract(inputPdf)
            val summerSemester = AcademicCalendarExtractor.summerSemester.extract(inputPdf)
            val summerSemester2 = AcademicCalendarExtractor.summerSemesterPage2.extract(inputPdf)

            return Try.map(
                headerText,
                winterSemester,
                summerSemester,
                summerSemester2
            ) { (text, winterSemester, summerSemester, summerSemester2) ->
                RawCalendarData(
                    text.first(),
                    text.dropLast(1),
                    winterSemester.first(),
                    summerSemester.first(),
                    text.last()
                )
            }.orThrow()
        } finally {
        }
    }

    @Bean
    fun createCalendarPDFDTOTasklet() = stepBuilderFactory.get("Create DTO from Calendar Raw Data")
        .tasklet { _, _ ->
            // TODO
            RepeatStatus.FINISHED
        }
        .build()

    @Bean
    fun writeCalendarDTOToGitTasklet() = stepBuilderFactory.get("Write Calendar DTO to Git")
        .tasklet { _, _ ->
            // TODO
            RepeatStatus.FINISHED
        }
        .build()

    @Bean
    fun sendNotificationsForCalendarJobTasklet() = stepBuilderFactory.get("Write Calendar DTO to Git")
        .tasklet { _, _ ->
            // TODO
            RepeatStatus.FINISHED
        }
        .build()

    @Component
    object State {
        lateinit var rawCalendarData: RawCalendarData
    }
}
