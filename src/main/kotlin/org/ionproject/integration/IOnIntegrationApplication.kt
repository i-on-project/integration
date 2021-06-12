package org.ionproject.integration

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.domain.model.InstitutionModel
import org.ionproject.integration.domain.model.ProgrammeModel
import org.ionproject.integration.infrastructure.error.ArgumentException
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.Instant

@SpringBootApplication
@EnableBatchProcessing
class IOnIntegrationApplication

const val LAUNCHER_NAME = "asyncLauncher"

@Configuration
class BatchConfig {

    @Autowired
    lateinit var jobRepository: JobRepository

    @Bean(name = [LAUNCHER_NAME])
    fun getAsyncLauncher(): JobLauncher = SimpleJobLauncher().apply {
        setJobRepository(jobRepository)
        setTaskExecutor(SimpleAsyncTaskExecutor())
        afterPropertiesSet()
    }
}

fun main(args: Array<String>) {
    runApplication<IOnIntegrationApplication>(*args)
}

@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {
    private val logger = LoggerFactory.getLogger(ErrorHandler::class.java)

    // TODO: Use json+problem (?)
    @ExceptionHandler(value = [ArgumentException::class])
    fun handle(exception: ArgumentException, request: WebRequest): ResponseEntity<Any> {
        logger.error("Error processing request $request: ${exception.message}")
        return handleExceptionInternal(exception, exception.message, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }
}

@Profile("!test")
@Component
class JobEngine(
    @Qualifier(LAUNCHER_NAME)
    private val jobLauncher: JobLauncher,
    private val ctx: ConfigurableApplicationContext
) {

    companion object {
        const val TIMETABLE_JOB_NAME = "timetable"
        const val EVALUATION_JOB_NAME = "evaluation"
        const val CALENDAR_JOB_NAME = "calendar"
        const val TIMESTAMP_PARAMETER = "timestamp"
        const val REMOTE_FILE_LOCATION_PARAMETER = "srcRemoteLocation"
        const val FORMAT_PARAMETER = "format"
        const val JOB_ID_PARAMETER = "jobId"
    }

    private val log = LoggerFactory.getLogger(IOnIntegrationApplication::class.java)

    @Autowired
    private lateinit var props: AppProperties

    fun runTimetableJob(request: TimetableJobRequest): JobStatus {
        val jobParams = getJobParameters(request, TIMETABLE_JOB_NAME)
        return runJob(TIMETABLE_JOB_NAME, jobParams)
    }

    fun runCalendarJob(request: CalendarJobRequest): JobStatus {
        val jobParams = getJobParameters(request, CALENDAR_JOB_NAME)
        return runJob(CALENDAR_JOB_NAME, jobParams)
    }

    fun getJobParameters(request: AbstractJobRequest, jobName: String): JobParameters {
        val parametersBuilder = JobParametersBuilder()

        val uri = when (request) {
            is TimetableJobRequest -> request.programme.timetableUri
            is CalendarJobRequest -> request.institution.academicCalendarUri
        }

        parametersBuilder.addLong(TIMESTAMP_PARAMETER, Instant.now().epochSecond)
        parametersBuilder.addString(REMOTE_FILE_LOCATION_PARAMETER, uri.toString())
        parametersBuilder.addString(FORMAT_PARAMETER, request.format.name)

        val jobHash = jobName.hashCode() + request.hashCode()
        parametersBuilder.addString(JOB_ID_PARAMETER, jobHash.toString())

        return parametersBuilder.toJobParameters()
    }

    fun runJob(jobName: String, parameters: JobParameters): JobStatus {
        val jobExecution = runCatching {
            val job = ctx.getBean("${jobName}Job", Job::class.java)
            jobLauncher.run(job, parameters)
        }
            .onFailure { return JobStatus(result = JobExecutionResult.CREATION_FAILED) }
            .getOrThrow()

        return JobStatus(jobExecution.jobId, JobExecutionResult.CREATED)
    }

    sealed class AbstractJobRequest(
        val format: OutputFormat,
        val institution: InstitutionModel
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AbstractJobRequest

            if (format != other.format) return false
            if (institution != other.institution) return false

            return true
        }

        override fun hashCode(): Int {
            var result = format.hashCode()
            result = 31 * result + institution.hashCode()
            return result
        }
    }

    class CalendarJobRequest(
        format: OutputFormat,
        institution: InstitutionModel
    ) : AbstractJobRequest(format, institution)

    class TimetableJobRequest(
        format: OutputFormat,
        institution: InstitutionModel,
        val programme: ProgrammeModel
    ) : AbstractJobRequest(format, institution) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as TimetableJobRequest

            if (programme != other.programme) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + programme.hashCode()
            return result
        }
    }

    data class JobStatus(val jobId: Long? = null, val result: JobExecutionResult)

    enum class JobExecutionResult {
        CREATED,
        CREATION_FAILED
    }
}
