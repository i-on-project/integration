package org.ionproject.integration.application.job

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.job.chunkbased.AlertOnFailureWriter
import org.ionproject.integration.application.job.chunkbased.ExtractReader
import org.ionproject.integration.application.job.chunkbased.FormatVerifierProcessor
import org.ionproject.integration.application.job.chunkbased.FormatVerifierStepBuilder
import org.ionproject.integration.application.job.tasklet.DownloadAndCompareTasklet
import org.ionproject.integration.application.job.tasklet.MappingTasklet
import org.ionproject.integration.application.job.tasklet.PostUploadTasklet
import org.ionproject.integration.application.job.tasklet.WriteFileTasklet
import org.ionproject.integration.domain.timetable.ISELTimetableFormatChecker
import org.ionproject.integration.domain.timetable.TimetableTeachers
import org.ionproject.integration.domain.timetable.dto.RawTimetableData
import org.ionproject.integration.infrastructure.file.FileComparatorImpl
import org.ionproject.integration.infrastructure.file.FileDigestImpl
import org.ionproject.integration.infrastructure.hash.HashRepositoryImpl
import org.ionproject.integration.infrastructure.http.IFileDownloader
import org.ionproject.integration.infrastructure.pdfextractor.PDFBytesFormatChecker
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.flow.Flow
import org.springframework.batch.core.job.flow.support.SimpleFlow
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Component
import javax.sql.DataSource

const val TIMETABLE_JOB_NAME = "timetable"

@Configuration
class ISELTimetableJob(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val properties: AppProperties,
    val downloader: IFileDownloader,
    @Autowired
    val ds: DataSource
) {

    @Bean(name = [TIMETABLE_JOB_NAME])
    fun timetableJob() = jobBuilderFactory.get(TIMETABLE_JOB_NAME)
        .start(taskletStep("Download And Compare", downloadAndCompareTasklet()))
        .on("STOPPED").end()
        .next(formatVerifierStep())
        .next(taskletStep("RawData to Business Object", mappingTasklet()))
        .next(writeLocalStep())
        .next(taskletStep("PostUpload", postUploadTasklet()))
        .build().build()

    private fun taskletStep(name: String, tasklet: Tasklet): TaskletStep {
        return stepBuilderFactory
            .get(name)
            .tasklet(tasklet)
            .build()
    }

    @StepScope
    @Bean
    fun downloadAndCompareTasklet(): DownloadAndCompareTasklet {
        val pdfChecker = PDFBytesFormatChecker()
        val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        return DownloadAndCompareTasklet(downloader, pdfChecker, fileComparator)
    }

    @StepScope
    @Bean
    fun extractReader() = ExtractReader()

    @Bean
    fun formatVerifierStep() = FormatVerifierStepBuilder(stepBuilderFactory)
        .build(
            extractReader(),
            FormatVerifierProcessor(State, ISELTimetableFormatChecker()),
            alertOnFailureWriter()
        )

    @Bean
    @StepScope
    fun alertOnFailureWriter() = AlertOnFailureWriter()

    @Bean
    @StepScope
    fun mappingTasklet() =
        MappingTasklet()

    fun flow(name: String, step: Step): Flow {
        return FlowBuilder<SimpleFlow>(name)
            .from(step)
            .end()
    }

    @Bean
    fun taskExecutor(): TaskExecutor {
        return SimpleAsyncTaskExecutor("spring_batch")
    }

    @Bean
    fun writeLocalStep(): Step {
        val writeLocalFlow = FlowBuilder<SimpleFlow>("Write to Git")
            .split(taskExecutor())
            .add(flow("Write Timetable", writeTimetableStep()))
            .build()

        return stepBuilderFactory.get("Write to Git").flow(writeLocalFlow)
            .build()
    }

    @Autowired
    private lateinit var timetableWriteTasklet: WriteFileTasklet

    @Bean
    fun writeTimetableStep(): TaskletStep {

        return taskletStep(
            "Submit Timetable data to Git",
            timetableWriteTasklet
        )
    }

    @Bean
    @StepScope
    fun postUploadTasklet() =
        PostUploadTasklet()

    @Component
    object State {
        lateinit var rawTimetableData: RawTimetableData
        lateinit var timetableTeachers: TimetableTeachers
    }
}
