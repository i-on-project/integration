package org.ionproject.integration.application.job

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dispatcher.DispatchResult
import org.ionproject.integration.application.dispatcher.IDispatcher
import org.ionproject.integration.application.dto.AcademicCalendarData
import org.ionproject.integration.application.job.tasklet.DownloadAndCompareTasklet
import org.ionproject.integration.domain.calendar.AcademicCalendar
import org.ionproject.integration.domain.calendar.AcademicCalendarDto
import org.ionproject.integration.domain.calendar.RawCalendarData
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.infrastructure.Try
import org.ionproject.integration.infrastructure.file.FileComparatorImpl
import org.ionproject.integration.infrastructure.file.FileDigestImpl
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.ionproject.integration.infrastructure.hash.HashRepositoryImpl
import org.ionproject.integration.infrastructure.http.IFileDownloader
import org.ionproject.integration.infrastructure.orThrow
import org.ionproject.integration.infrastructure.pdfextractor.AcademicCalendarExtractor
import org.ionproject.integration.infrastructure.pdfextractor.ITextPdfExtractor
import org.ionproject.integration.infrastructure.pdfextractor.PDFBytesFormatChecker
import org.ionproject.integration.infrastructure.repository.IInstitutionRepository
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.io.File
import javax.sql.DataSource

const val CALENDAR_JOB_NAME = "calendar"

@Configuration
class ISELAcademicCalendarJob(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val properties: AppProperties,
    val downloader: IFileDownloader,
    val dispatcher: IDispatcher,
    val institutionRepository: IInstitutionRepository,
    @Autowired
    val ds: DataSource
) {

    @Bean(name = [CALENDAR_JOB_NAME])
    fun calendarJob() = jobBuilderFactory.get(CALENDAR_JOB_NAME)
        .start(taskletStep("Download And Compare", downloadCalendarPDFTasklet()))
        .on("STOPPED").end()
        .next(extractCalendarPDFTasklet())
        .next(createCalendarPDFBusinessObjectsTasklet())
        .next(createCalendarPDFDtoTasklet())
        .next(writeCalendarDTOToGitTasklet())
        .build()
        .listener(NotificationListener())
        .build()

    private fun taskletStep(name: String, tasklet: Tasklet): TaskletStep {
        return stepBuilderFactory
            .get(name)
            .tasklet(tasklet)
            .build()
    }

    @StepScope
    @Bean
    fun downloadCalendarPDFTasklet(): DownloadAndCompareTasklet {
        val pdfChecker = PDFBytesFormatChecker()
        val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        return DownloadAndCompareTasklet(downloader, pdfChecker, fileComparator)
    }

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
            if (!path.contains("test"))
                File(path).delete()
        }
    }

    @Bean
    fun createCalendarPDFBusinessObjectsTasklet() =
        stepBuilderFactory.get("Create Business Objects from Calendar Raw Data")
            .tasklet { _, context ->
                State.academicCalendar = AcademicCalendar.from(State.rawCalendarData, getJobInstitution(context))
                RepeatStatus.FINISHED
            }
            .build()

    private fun getJobInstitution(context: ChunkContext): InstitutionModel =
        institutionRepository.getInstitutionByIdentifier(
            context.stepContext.jobParameters[JobEngine.INSTITUTION_PARAMETER] as String
        )

    @Bean
    fun createCalendarPDFDtoTasklet() = stepBuilderFactory.get("Create DTO from Calendar Business Objects")
        .tasklet { _, _ ->
            State.academicCalendarDto = AcademicCalendarDto.from(State.academicCalendar)
            RepeatStatus.FINISHED
        }
        .build()

    @Bean
    fun writeCalendarDTOToGitTasklet() = stepBuilderFactory.get("Write Calendar DTO to Git")
        .tasklet { stepContribution, context ->
            val formatParam = context.stepContext.jobParameters[JobEngine.FORMAT_PARAMETER] as String
            val identifier = context.stepContext.jobParameters[JobEngine.INSTITUTION_PARAMETER] as String
            val format = OutputFormat.of(formatParam)
            val calendarData = AcademicCalendarData.from(State.academicCalendarDto, identifier)

            val dispatchResult = dispatcher.dispatch(calendarData, CALENDAR_JOB_NAME, format)

            if (dispatchResult == DispatchResult.FAILURE) {
                stepContribution.exitStatus = ExitStatus.FAILED
            }
            RepeatStatus.FINISHED
        }
        .build()

    @Component
    object State {
        lateinit var rawCalendarData: RawCalendarData
        lateinit var academicCalendar: AcademicCalendar
        lateinit var academicCalendarDto: AcademicCalendarDto
    }
}
