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
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.PropertiesLoaderUtils

@SpringBootApplication
@EnableBatchProcessing
class IOnIntegrationApplication

    lateinit var ctx: ConfigurableApplicationContext
    lateinit var jobLauncher: JobLauncher
    private val log = LoggerFactory.getLogger(IOnIntegrationApplication::class.java)
    fun main(args: Array<String>) {
        ctx = SpringApplication
            .run(IOnIntegrationApplication::class.java, *args)
        jobLauncher = ctx.getBean(JobLauncher::class.java)

        runJob("timetableJob", "/app/config/timetable/isel")
        runJob("genericJob", "/app/config/generic/academic-calendar")
        runJob("genericJob", "/app/config/generic/exam-schedule")
    }

    fun runJob(jobName: String, configPath: String) {
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
