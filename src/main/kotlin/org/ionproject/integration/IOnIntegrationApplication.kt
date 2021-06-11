package org.ionproject.integration

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.domain.model.InstitutionModel
import org.ionproject.integration.domain.model.ProgrammeModel
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import org.springframework.stereotype.Component
import java.io.File
import java.time.Instant

@SpringBootApplication
@EnableBatchProcessing
class IOnIntegrationApplication

fun main(args: Array<String>) {
    runApplication<IOnIntegrationApplication>(*args)
}

@Profile("!test")
@Component
class JobEngine(
    private val jobLauncher: JobLauncher,
    private val ctx: ConfigurableApplicationContext
) {

    companion object {
        const val TIMETABLE_JOB_NAME = "timetable"
        const val EXAM_SCHEDULE_JOB_NAME = "evaluation_schedule"
        const val ACADEMIC_CALENDAR_JOB_NAME = "academic_calendar"
    }

    private val log = LoggerFactory.getLogger(IOnIntegrationApplication::class.java)

    @Autowired
    private lateinit var props: AppProperties

    fun runTimetableJob(request: TimetableJobRequest): JobStatus {
        return setUpAndRunJob("timetableJob", props.configFilesDirTimetableIsel.path)
    }

    fun runCalendarJob(request: CalendarJobRequest): JobStatus {
        return setUpAndRunJob("calendarJob", props.configFilesDirCalendarIsel.path)
    }

    fun setUpAndRunJob(jobName: String, configPath: String): JobStatus {
        val props = File(configPath)
            .listFiles()
            ?.map {
                val resource = FileSystemResource(it.absolutePath)
                PropertiesLoaderUtils.loadProperties(resource)
            }

        if (props == null) {
            log.warn("$configPath has no files. Consider adding properties files for job $jobName to $configPath")
            return JobStatus(result = JobExecutionResult.CREATION_FAILED)
        }
        props.forEach {
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

            val jobExecution = jobLauncher.run(job, jobParametersBuilder.toJobParameters())

            return JobStatus(jobExecution.jobId, JobExecutionResult.CREATED)
        }

        return JobStatus(result = JobExecutionResult.CREATION_FAILED)
    }

    data class CalendarJobRequest(
        val format: OutputFormat,
        val institution: InstitutionModel
    )

    data class TimetableJobRequest(
        val format: OutputFormat,
        val institution: InstitutionModel,
        val programme: ProgrammeModel
    )

    data class JobStatus(val jobId: Long? = null, val result: JobExecutionResult)

    enum class JobExecutionResult {
        CREATED,
        CREATION_FAILED
    }
}
