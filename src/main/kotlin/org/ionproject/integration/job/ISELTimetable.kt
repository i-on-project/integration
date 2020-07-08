package org.ionproject.integration.job

import org.ionproject.integration.format.implementations.ISELTimetableFormatChecker
import org.ionproject.integration.model.external.timetable.TimetableTeachers
import org.ionproject.integration.model.internal.timetable.UploadType
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.step.chunkbased.FormatVerifierStepBuilder
import org.ionproject.integration.step.chunkbased.processor.FormatVerifierProcessor
import org.ionproject.integration.step.chunkbased.reader.ExtractReader
import org.ionproject.integration.step.chunkbased.writer.AlertOnFailureWriter
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.DownloadAndCompareTasklet
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.MappingTasklet
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.PostUploadTasklet
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.UploadTasklet
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

@Configuration
class ISELTimetable(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory
) {
    @Bean
    fun timetableJob() = jobBuilderFactory.get("ISEL Timetable Batch Job")
        .start(taskletStep("Download And Compare", downloadAndCompareTasklet()))
        .on("STOPPED").end()
        .next(formatVerifierStep())
        .next(taskletStep("RawData to Business Object", mappingTasklet()))
        .next(uploadStep())
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
    fun downloadAndCompareTasklet() =
        DownloadAndCompareTasklet()

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

    @Bean
    fun uploadStep(): Step {
        val uploadFlow = FlowBuilder<SimpleFlow>("Upload to I-On Core")
            .split(taskExecutor())
            .add(
                flow("Upload Faculty", facultyStep()),
                flow("Upload Timetable", timetableStep())
            ).build()

        return stepBuilderFactory.get("Upload to I-On Core").flow(uploadFlow)
            .build()
    }

    fun flow(name: String, step: Step): Flow {
        return FlowBuilder<SimpleFlow>(name)
            .from(step)
            .end()
    }

    @Bean
    fun taskExecutor(): TaskExecutor {
        return SimpleAsyncTaskExecutor("spring_batch")
    }

    @Autowired
    private lateinit var timetableTasklet: UploadTasklet

    @Bean
    fun timetableStep(): TaskletStep {
        timetableTasklet.setUploadType(UploadType.TIMETABLE)

        return taskletStep(
            "Upload Timetable Information to I-On Core",
            timetableTasklet)
    }

    @Autowired
    private lateinit var facultyTasklet: UploadTasklet

    @Bean
    fun facultyStep(): TaskletStep {
        facultyTasklet.setUploadType(UploadType.TEACHERS)

        return taskletStep(
            "Upload Faculty Information to I-On Core",
            facultyTasklet
        )
    }

    @Bean
    @StepScope
    fun postUploadTasklet() =
        PostUploadTasklet()

    @Component
    object State {
        lateinit var rawData: RawData
        lateinit var timetableTeachers: TimetableTeachers
    }
}
