package org.ionproject.integration.job

import org.ionproject.integration.step.tasklet.iseltimetable.implementations.DownloadAndCompareTasklet
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ISELTimetable {

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Bean
    fun downloadAndCompareStep() = stepBuilderFactory.get("Download And Compare")
        .tasklet(downloadAndCompareTasklet())
        .build()

    @Bean
    fun downloadAndCompareTasklet() =
        DownloadAndCompareTasklet()

    @Bean
    fun timetableJob() = jobBuilderFactory.get("ISEL Timetable Batch Job")
        .start(downloadAndCompareStep())
        .build()
}
