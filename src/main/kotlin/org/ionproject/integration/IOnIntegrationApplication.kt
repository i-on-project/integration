package org.ionproject.integration

import java.io.File
import java.time.Instant
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

    fun main(args: Array<String>) {
        ctx = SpringApplication
            .run(IOnIntegrationApplication::class.java, *args)
        jobLauncher = ctx.getBean(JobLauncher::class.java)

        runJob("timetableJob", "src/main/resources/config/timetable/isel")
    }

    fun runJob(jobName: String, configPath: String) {
        val props = File(configPath)
            .listFiles()
            ?.map {
                val resource = FileSystemResource(it.absolutePath)
                PropertiesLoaderUtils.loadProperties(resource)
            }
            ?: return

        props
            .forEach {
                val job = ctx.getBean(jobName, Job::class.java)
                val jobParametersBuilder = JobParametersBuilder()

                jobParametersBuilder.addLong("timestamp", Instant.now().epochSecond)

                it.forEach { p ->
                    jobParametersBuilder.addString(p.key.toString(), p.value.toString())
                }

                jobLauncher.run(job, jobParametersBuilder.toJobParameters())
            }
    }
