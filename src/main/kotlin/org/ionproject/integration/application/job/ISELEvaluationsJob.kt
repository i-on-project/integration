package org.ionproject.integration.application.job

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dispatcher.IDispatcher
import org.ionproject.integration.application.job.tasklet.DownloadAndCompareTasklet
import org.ionproject.integration.domain.evaluations.Evaluations
import org.ionproject.integration.domain.evaluations.EvaluationsDto
import org.ionproject.integration.domain.evaluations.RawEvaluationsData
import org.ionproject.integration.infrastructure.Try
import org.ionproject.integration.infrastructure.file.FileComparatorImpl
import org.ionproject.integration.infrastructure.file.FileDigestImpl
import org.ionproject.integration.infrastructure.hash.HashRepositoryImpl
import org.ionproject.integration.infrastructure.http.IFileDownloader
import org.ionproject.integration.infrastructure.orThrow
import org.ionproject.integration.infrastructure.pdfextractor.EvaluationsExtractor
import org.ionproject.integration.infrastructure.pdfextractor.ITextPdfExtractor
import org.ionproject.integration.infrastructure.pdfextractor.PDFBytesFormatChecker
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
import java.io.File
import javax.sql.DataSource

const val EVALUATIONS_JOB_NAME = "evaluations"

@Configuration
class ISELEvaluationsJob(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val properties: AppProperties,
    val downloader: IFileDownloader,
    val dispatcher: IDispatcher,
    @Autowired
    val ds: DataSource
) {

    @Bean(name = [EVALUATIONS_JOB_NAME])
    fun calendarJob() = jobBuilderFactory.get(EVALUATIONS_JOB_NAME)
        .start(taskletStep("Download And Compare", downloadEvaluationsPDFTasklet()))
        .on("STOPPED").end()
        .next(extractEvaluationsPDFTasklet())
        //    .next(createEvaluationsPDFBusinessObjectsTasklet())
        //    .next(createEvaluationsPDFDtoTasklet())
        //    .next(writeEvaluationsDTOToGitTasklet())
        .build().listener(NotificationListener())
        .build()

    private fun taskletStep(name: String, tasklet: Tasklet): TaskletStep {
        return stepBuilderFactory
            .get(name)
            .tasklet(tasklet)
            .build()
    }

    @StepScope
    @Bean
    fun downloadEvaluationsPDFTasklet(): DownloadAndCompareTasklet {
        val pdfChecker = PDFBytesFormatChecker()
        val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        return DownloadAndCompareTasklet(downloader, pdfChecker, fileComparator)
    }

/*    @Bean
    fun downloadEvaluationsPDFTasklet() = stepBuilderFactory.get("Download Calendar PDF")
        .tasklet { stepContribution, chunkContext ->
            val pdfChecker = PDFBytesFormatChecker()
            val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
            DownloadAndCompareTasklet(downloader, pdfChecker, fileComparator).execute(stepContribution, chunkContext)
        }
        .build()*/

    @Bean
    fun extractEvaluationsPDFTasklet() = stepBuilderFactory.get("Extract Evaluations PDF Raw Data")
        .tasklet { stepContribution, context ->
            val path = stepContribution.stepExecution.jobExecution.executionContext.get("file-path").toString()
            val school = context.stepContext.jobParameters[JobEngine.INSTITUTION_PARAMETER] as String
            State.rawEvaluationsData = extractEvaluationsPDF(path)
            RepeatStatus.FINISHED
        }
        .build()

    fun extractEvaluationsPDF(path: String): RawEvaluationsData {
        try {
            val itext = ITextPdfExtractor()

            val headerText = itext.extract(path)
            val evaluationsTable = EvaluationsExtractor.evaluationsTable.extract(path)

            return Try.map(
                headerText,
                evaluationsTable
            ) { (text, evaluationsTable) ->
                RawEvaluationsData(
                    text.dropLast(1),
                    evaluationsTable.first().replace("\\r", " "),
                    text.last()
                )
            }.orThrow()
        } finally {
            File(path).delete()
        }
    }

    @Bean
    fun createEvaluationsPDFBusinessObjectsTasklet() =
        stepBuilderFactory.get("Create Business Objects from Evaluations Raw Data")
            .tasklet { _, _ ->
                State.evaluations = Evaluations.from(State.rawEvaluationsData)
                RepeatStatus.FINISHED
            }
            .build()

    @Bean
    fun createEvaluationsPDFDtoTasklet() = stepBuilderFactory.get("Create DTO from Evaluations Business Objects")
        .tasklet { _, _ ->
            State.evaluationsDto = EvaluationsDto.from(State.evaluations)
            RepeatStatus.FINISHED
        }
        .build()

    // TODO
    @Bean
    fun writeEvaluationsDTOToGitTasklet() = stepBuilderFactory.get("Write Calendar DTO to Git")
        .tasklet { _, _ ->
/*            dispatcher.dispatch(
                EvaluationsData.from(State.evaluationsDto),
                EVALUATIONS_JOB_NAME,
                OutputFormat.JSON
            )*/
            RepeatStatus.FINISHED
        }
        .build()

    @Component
    object State {
        lateinit var rawEvaluationsData: RawEvaluationsData
        lateinit var evaluations: Evaluations
        lateinit var evaluationsDto: EvaluationsDto
    }
}
