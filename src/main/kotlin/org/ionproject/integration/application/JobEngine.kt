package org.ionproject.integration.application

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.config.LAUNCHER_NAME
import org.ionproject.integration.infrastructure.OutputFormat
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.infrastructure.repository.IIntegrationJobRepository
import org.ionproject.integration.job.CALENDAR_JOB_NAME
import org.ionproject.integration.job.TIMETABLE_JOB_NAME
import org.ionproject.integration.application.job.JobType
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import java.net.URI
import java.time.Instant
import java.time.LocalDate

@Component
class JobEngine(
    @Qualifier(LAUNCHER_NAME)
    private val jobLauncher: JobLauncher,
    private val ctx: ConfigurableApplicationContext,
    private val integrationJobRepository: IIntegrationJobRepository
) {

    companion object {
        const val TIMESTAMP_PARAMETER = "timestamp"
        const val REMOTE_FILE_LOCATION_PARAMETER = "srcRemoteLocation"
        const val FORMAT_PARAMETER = "format"
        const val JOB_HASH_PARAMETER = "jobHash"
        const val INSTITUTION_PARAMETER = "institution"
        const val PROGRAMME_PARAMETER = "programme"
        const val JOB_TYPE_PARAMETER = "jobType"
    }

    private val log = LoggerFactory.getLogger(JobEngine::class.java)

    @Autowired
    private lateinit var props: AppProperties

    fun runJob(request: AbstractJobRequest): JobStatus {
        return when (request) {
            is TimetableJobRequest -> runTimetableJob(request)
            is CalendarJobRequest -> runCalendarJob(request)
        }
    }

    fun getRunningJobs(): List<IntegrationJob> = integrationJobRepository.getRunningJobs()

    private fun runTimetableJob(request: TimetableJobRequest): JobStatus {
        val jobParams = getJobParameters(request, TIMETABLE_JOB_NAME)
        return runJob(TIMETABLE_JOB_NAME, jobParams)
    }

    private fun runCalendarJob(request: CalendarJobRequest): JobStatus {
        val jobParams = getJobParameters(request, CALENDAR_JOB_NAME)
        return runJob(CALENDAR_JOB_NAME, jobParams)
    }

    private fun getJobParameters(request: AbstractJobRequest, jobName: String): JobParameters {
        val parametersBuilder = JobParametersBuilder()

        val uri = when (request) {
            is TimetableJobRequest ->
                request.programme.timetableUri.also {
                    parametersBuilder.addString(PROGRAMME_PARAMETER, request.programme.acronym)
                }
            is CalendarJobRequest -> request.institution.academicCalendarUri
        }

        parametersBuilder.addLong(TIMESTAMP_PARAMETER, Instant.now().epochSecond)
        parametersBuilder.addString(REMOTE_FILE_LOCATION_PARAMETER, uri.toString())
        parametersBuilder.addString(FORMAT_PARAMETER, request.format.name)
        parametersBuilder.addString(INSTITUTION_PARAMETER, request.institution.identifier)
        parametersBuilder.addString(JOB_TYPE_PARAMETER, request.jobType.identifier)

        val jobHash = jobName.hashCode() + request.hashCode()
        parametersBuilder.addString(JOB_HASH_PARAMETER, jobHash.toString())

        return parametersBuilder.toJobParameters()
    }

    private fun runJob(jobName: String, parameters: JobParameters): JobStatus {
        val jobExecution = runCatching {
            val job = ctx.getBean(jobName, Job::class.java)
            jobLauncher.run(job, parameters)
        }
            .onFailure { return JobStatus(result = JobExecutionResult.CREATION_FAILED) }
            .getOrThrow()

        return JobStatus(jobExecution.jobId, JobExecutionResult.CREATED)
    }

    sealed class AbstractJobRequest(
        val format: OutputFormat,
        val institution: InstitutionModel,
        val jobType: JobType
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
    ) : AbstractJobRequest(format, institution, JobType.ACADEMIC_CALENDAR)

    class TimetableJobRequest(
        format: OutputFormat,
        institution: InstitutionModel,
        val programme: ProgrammeModel
    ) : AbstractJobRequest(format, institution, JobType.TIMETABLE) {
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

    data class IntegrationJob(
        val type: JobType,
        val status: JobStatus,
        val parameters: IntegrationJobParameters
    )

    data class IntegrationJobParameters(
        val creationDate: LocalDate,
        val format: OutputFormat,
        val institution: InstitutionModel,
        val programme: ProgrammeModel? = null,
        val uri: URI
    )

    data class JobStatus(val jobId: Long? = null, val result: JobExecutionResult)

    enum class JobExecutionResult {
        CREATED,
        CREATION_FAILED,
        RUNNING,
        FAILED,
        COMPLETED
    }
}