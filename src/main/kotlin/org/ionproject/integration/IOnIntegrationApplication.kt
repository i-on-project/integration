package org.ionproject.integration

import java.io.File
import java.time.Instant
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@SpringBootApplication
@EnableBatchProcessing
class IOnIntegrationApplication

    fun main(args: Array<String>) {
        SpringApplication.run(IOnIntegrationApplication::class.java, *args)
    }

@Profile("!test")
@Configuration
@EnableScheduling
class JobEngine(
    val jobLauncher: JobLauncher,
    val ctx: ConfigurableApplicationContext
) {
    private val log = LoggerFactory.getLogger(IOnIntegrationApplication::class.java)

    @Scheduled(fixedRate = 120000)
    fun runTimetableJob() {
        setUpAndRunJob("timetableJob", "/app/resources/config/timetable/isel")
    }

    @Scheduled(fixedRate = 120000)
    fun runGenericAcademicCalendar() {
        setUpAndRunJob("genericJob", "/app/resources/config/generic/academic-calendar")
    }

    @Scheduled(fixedRate = 120000)
    fun runGenericExamSchedule() {
        setUpAndRunJob("genericJob", "/app/resources/config/generic/exam-schedule")
    }

    fun setUpAndRunJob(jobName: String, configPath: String) {
        val props = File(configPath)
            .listFiles()
            ?.map {
                val resource = FileSystemResource(it.absolutePath)
                PropertiesLoaderUtils.loadProperties(resource)
            }

        if (props == null) {
            log.warn("$configPath has no files. Consider adding properties files for job $jobName to $configPath")
        } else {
            props
                .forEach {
                    val job = ctx.getBean(jobName, Job::class.java)
                    val jobParametersBuilder = JobParametersBuilder()

                    jobParametersBuilder.addLong("timestamp", Instant.now().epochSecond)

                    it.forEach { p ->
                        jobParametersBuilder.addString(p.key.toString(), p.value.toString())
                    }

                    // We want to detect executions of the job
                    // for the same course without peeking into the pdf.
                    // One way is to concat all the jobParameters
                    // with job name and calculate the hash of the resulting string.
                    // It does not detect all,
                    // but a reasonable amount of times.
                    val jobHash = jobName.plus(it.entries.joinToString()).hashCode()
                    jobParametersBuilder.addString("jobId", jobHash.toString())

                    jobLauncher.run(job, jobParametersBuilder.toJobParameters())
                }
        }
    }
}
