package org.ionproject.integration.job

import org.ionproject.integration.file.implementations.FileComparatorImpl
import org.ionproject.integration.file.implementations.FileDigestImpl
import org.ionproject.integration.file.implementations.FileDownloaderImpl
import org.ionproject.integration.file.implementations.PDFBytesFormatChecker
import org.ionproject.integration.format.implementations.ISELTimetableFormatChecker
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.ionproject.integration.model.external.timetable.TimetableTeachers
import org.ionproject.integration.model.internal.timetable.isel.RawTimetableData
import org.ionproject.integration.step.chunkbased.FormatVerifierStepBuilder
import org.ionproject.integration.step.chunkbased.processor.FormatVerifierProcessor
import org.ionproject.integration.step.chunkbased.reader.ExtractReader
import org.ionproject.integration.step.chunkbased.writer.AlertOnFailureWriter
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.DownloadAndCompareTasklet
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.MappingTasklet
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.PostUploadTasklet
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.WriteFileTasklet
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

@Configuration
class ISELTimetable(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    @Autowired
    val ds: DataSource
) {
    @Bean
    fun timetableJob() = jobBuilderFactory.get("ISEL Timetable Batch Job")
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
        val downloader = FileDownloaderImpl(pdfChecker)
        val fileComparator = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        return DownloadAndCompareTasklet(downloader, fileComparator)
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
        MappingTasklet(State)

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
        val writeLocalFlow = FlowBuilder<SimpleFlow>("Write Local Files")
            .split(taskExecutor())
            .add(
                flow("Write Timetable", writeTimetableStep())
            ).build()

        return stepBuilderFactory.get("Write Local Files").flow(writeLocalFlow)
            .build()
    }

    @Autowired
    private lateinit var timetableWriteTasklet: WriteFileTasklet

    @Bean
    fun writeTimetableStep(): TaskletStep {

        return taskletStep(
            "Write Timetable Information to Local Folder",
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
