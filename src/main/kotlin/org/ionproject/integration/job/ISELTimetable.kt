package org.ionproject.integration.job

import org.ionproject.integration.alert.implementations.EmailAlertChannel
import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.format.implementations.ISELTimetableFormatChecker
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.step.chunkbased.FormatVerifierStepBuilder
import org.ionproject.integration.step.chunkbased.processor.FormatVerifierProcessor
import org.ionproject.integration.step.chunkbased.reader.ExtractReader
import org.ionproject.integration.step.chunkbased.writer.AlertOnFailureWriter
import org.ionproject.integration.step.tasklet.iseltimetable.implementations.DownloadAndCompareTasklet
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class ISELTimetable(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val properties: ISELTimetableProperties
) {
    @Bean
    fun timetableJob() = jobBuilderFactory.get("ISEL Timetable Batch Job")
        .start(downloadAndCompareStep())
        .next(formatVerifierStep())
        .build()

    @Bean
    fun downloadAndCompareStep() = stepBuilderFactory.get("Download And Compare")
        .tasklet(downloadAndCompareTasklet(properties))
        .build()

    @Bean
    fun downloadAndCompareTasklet(props: ISELTimetableProperties) =
        DownloadAndCompareTasklet(props)

    @Bean
    fun formatVerifierStep() = FormatVerifierStepBuilder(stepBuilderFactory)
        .build(
            ExtractReader(properties),
            FormatVerifierProcessor(State, ISELTimetableFormatChecker()),
            AlertOnFailureWriter(properties, EmailAlertChannel(properties))
        )

    @Component
    object State {
        lateinit var rawData: RawData
    }
}
