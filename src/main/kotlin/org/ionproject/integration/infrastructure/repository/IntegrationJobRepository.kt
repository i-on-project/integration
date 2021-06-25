package org.ionproject.integration.infrastructure.repository

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.ionproject.integration.application.job.JobType
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
}

@Repository
class IntegrationJobRepository(
    private val dataSource: DataSource,
    private val institutionRepository: IInstitutionRepository,
    private val programmeRepository: IProgrammeRepository
) : IIntegrationJobRepository, ResultSetExtractor<List<JobEngine.IntegrationJob>> {

    val jdbc by lazy { JdbcTemplate(dataSource).apply { update(CREATE_JOBS_VIEW_QUERY) } }

    override fun getRunningJobs(): List<JobEngine.IntegrationJob> {
        return jdbc.query(SELECT_RUNNING_JOBS_QUERY, ::extractData) ?: emptyList()
    }

    override fun extractData(rs: ResultSet): List<JobEngine.IntegrationJob>? {
        val jobsData = mutableListOf<JobEngine.IntegrationJob>()
        while (rs.next())
            jobsData += getJobDataFromResultSet(rs)

        return jobsData
    }

    private fun getJobDataFromResultSet(resultSet: ResultSet): JobEngine.IntegrationJob {
        // TODO: create vars for indexes
        val id = resultSet.getLong(1)
        val jobIdentifier = resultSet.getString(2)
        val creationDate = resultSet.getDate(3)
        val startDate = resultSet.getDate(4)
        val status = resultSet.getString(5)
        val format = resultSet.getString(6)
        val institutionIdentifier = resultSet.getString(7)
        val programme = resultSet.getString(8)
        val uri = resultSet.getString(9)

        val jobType = JobType.of(jobIdentifier) ?: throw IllegalArgumentException("Invalid job: $jobIdentifier")

        val institution = institutionRepository.getInstitutionByIdentifier(institutionIdentifier)

        val parameters = JobEngine.IntegrationJobParameters(
            creationDate.toLocalDate(),
            startDate.toLocalDate(),
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
