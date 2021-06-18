package org.ionproject.integration.infrastructure.repository

import org.ionproject.integration.JobEngine
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.ui.dto.JobType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import java.lang.IllegalArgumentException
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

    init {
        JdbcTemplate(dataSource).execute(SETUP_EXTENSION_QUERY)
    }

    override fun getRunningJobs(): List<JobEngine.IntegrationJob> {
        return JdbcTemplate(dataSource).query(RUNNING_JOBS_QUERY, ::extractData) ?: emptyList()
    }

    override fun extractData(rs: ResultSet): List<JobEngine.IntegrationJob>? {
        val jobsData = mutableListOf<JobEngine.IntegrationJob>()
        while (rs.next()) jobsData += getJobDataFromResultSet(rs)

        return jobsData
    }

    private fun getJobDataFromResultSet(resultSet: ResultSet): JobEngine.IntegrationJob {
        val id = resultSet.getLong(1)
        val jobIdentifier = resultSet.getString(2)
        val creationDate = resultSet.getDate(3)
        val format = resultSet.getString(4)
        val institutionIdentifier = resultSet.getString(5)
        val programme = resultSet.getString(6)
        val uri = resultSet.getString(7)

        val jobType =
            JobType.of(jobIdentifier) ?: throw IllegalArgumentException("Invalid job identifier: $jobIdentifier")

        val institution = institutionRepository.getInstitutionByIdentifier(institutionIdentifier)

        val parameters = JobEngine.IntegrationJobParameters(
            creationDate.toLocalDate(),
            OutputFormat.of(format),
            institution,
            if (jobType != JobType.ACADEMIC_CALENDAR)
                programmeRepository.getProgrammeByAcronymAndInstitution(programme, institution)
            else null,
            URI.create(uri)
        )

        val jobStatus = JobEngine.JobStatus(id, JobEngine.JobExecutionResult.RUNNING)
        return JobEngine.IntegrationJob(jobType, jobStatus, parameters)
    }
}
