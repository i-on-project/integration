package org.ionproject.integration.job

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class ISELAcademicCalendar(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    @Autowired
    val ds: DataSource
) {
    @Bean
    fun academicCalendarJob() = jobBuilderFactory.get("ISEL Academic Calendar Batch Job")
}
