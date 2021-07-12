package org.ionproject.integration.infrastructure.repository.job

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.ionproject.integration.application.job.JobType
import org.ionproject.integration.infrastructure.repository.CREATE_JOBS_VIEW_QUERY
import org.ionproject.integration.infrastructure.repository.model.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.model.IProgrammeRepository
import org.ionproject.integration.infrastructure.repository.JOB_DETAILS_QUERY
import org.ionproject.integration.infrastructure.repository.JobQueryFields
import org.ionproject.integration.infrastructure.repository.RUNNING_JOBS_QUERY
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.net.URI
import java.sql.ResultSet
import javax.sql.DataSource

interface IIntegrationJobRepository {
    fun getRunningJobs(): List<JobEngine.IntegrationJob>
    fun getJobById(id: Long): JobEngine.IntegrationJob?
}

@Repository
class IntegrationJobRepository(
    private val dataSource: DataSource,
    private val institutionRepository: IInstitutionRepository,
    private val programmeRepository: IProgrammeRepository
) : IIntegrationJobRepository, ResultSetExtractor<List<JobEngine.IntegrationJob>> {

    val jdbc by lazy { JdbcTemplate(dataSource).apply { update(CREATE_JOBS_VIEW_QUERY) } }

    override fun getRunningJobs(): List<JobEngine.IntegrationJob> {
        return jdbc.query(RUNNING_JOBS_QUERY, ::extractData) ?: emptyList()
    }

    override fun getJobById(id: Long): JobEngine.IntegrationJob? =
        jdbc.query(
            { conn -> conn.prepareStatement(JOB_DETAILS_QUERY) },
            { statement -> statement.setLong(1, id) }
        ) {
            if (it.next())
                getJobDataFromResultSet(it)
            else
                null
        }

    override fun extractData(rs: ResultSet): List<JobEngine.IntegrationJob>? {
        val jobsData = mutableListOf<JobEngine.IntegrationJob>()
        while (rs.next())
            jobsData += getJobDataFromResultSet(rs)

        return jobsData
    }

    private fun getJobDataFromResultSet(resultSet: ResultSet): JobEngine.IntegrationJob {
        val id = resultSet.getLong(JobQueryFields.ID.index)
        val jobIdentifier = resultSet.getString(JobQueryFields.NAME.index)
        val creationDate = resultSet.getTimestamp(JobQueryFields.CREATION_DATE.index)
        val startDate = resultSet.getTimestamp(JobQueryFields.START_DATE.index)
        val endDate = resultSet.getTimestamp(JobQueryFields.END_DATE.index)
        val status = resultSet.getString(JobQueryFields.STATUS.index)
        val format = resultSet.getString(JobQueryFields.FORMAT.index)
        val institutionIdentifier = resultSet.getString(JobQueryFields.INSTITUTION.index)
        val programme = resultSet.getString(JobQueryFields.PROGRAMME.index)
        val uri = resultSet.getString(JobQueryFields.URI.index)

        val jobType = JobType.of(jobIdentifier) ?: throw IllegalArgumentException("Invalid job: $jobIdentifier")

        val institution = institutionRepository.getInstitutionByIdentifier(institutionIdentifier)

        val parameters = JobEngine.IntegrationJobParameters(
            creationDate.toLocalDateTime(),
            startDate?.toLocalDateTime(),
            endDate?.toLocalDateTime(),
            OutputFormat.of(format),
            institution,
            if (jobType != JobType.ACADEMIC_CALENDAR)
                programmeRepository.getProgrammeByAcronymAndInstitution(programme, institution)
            else null,
            URI.create(uri)
        )

        val jobResult = getJobResultFromString(status)
        val jobStatus = JobEngine.JobStatus(id, jobResult)
        return JobEngine.IntegrationJob(jobType, jobStatus, parameters)
    }

    private fun getJobResultFromString(statusText: String): JobEngine.JobExecutionResult =
        when (statusText) {
            "STARTING" -> JobEngine.JobExecutionResult.CREATED
            "STARTED" -> JobEngine.JobExecutionResult.RUNNING
            "COMPLETED" -> JobEngine.JobExecutionResult.COMPLETED
            "FAILED" -> JobEngine.JobExecutionResult.FAILED
            else -> throw IllegalStateException("Invalid job state: $statusText")
        }
}
