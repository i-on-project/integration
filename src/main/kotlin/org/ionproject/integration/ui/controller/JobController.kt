package org.ionproject.integration.ui.controller

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.ui.dto.CreateJobDto
import org.ionproject.integration.ui.dto.InputProcessor
import org.ionproject.integration.ui.dto.JobDetailDto
import org.ionproject.integration.ui.dto.PostResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal const val JOBS_URI = "/jobs"

@RestController
@RequestMapping(JOBS_URI)
class JobController(
    val jobEngine: JobEngine,
    val inputProcessor: InputProcessor,
) {

    private val logger = LoggerFactory.getLogger(JobController::class.java)

    @PostMapping(consumes = ["application/json"])
    fun createTimetableJob(
        @RequestBody body: CreateJobDto,
        servletRequest: HttpServletRequest,
        response: HttpServletResponse
    ): PostResponse {
        val request = inputProcessor.getJobRequest(body)
        val requestResult = jobEngine.runJob(request)

        return when (requestResult.result) {
            JobEngine.JobExecutionResult.CREATED -> {
                PostResponse(
                    location = servletRequest.getLocationForJobRequest(requestResult),
                    status = HttpStatus.CREATED,
                    response = response
                )
            }
            else -> {
                logger.error("Job creation failed: $body")
                PostResponse(
                    status = HttpStatus.BAD_REQUEST,
                    response = response
                )
            }
        }
    }

    @GetMapping
    fun getJobs(servletRequest: HttpServletRequest): List<JobDetailDto> =
        jobEngine.getRunningJobs()
            .map { job ->
                val url = servletRequest.getLocationForJobRequest(job.status)
                JobDetailDto.of(job, url, JobDetailDto.DetailType.METADATA_ONLY)
            }

    @GetMapping("/{id}")
    fun getJobDetails(
        @PathVariable id: Long,
        servletRequest: HttpServletRequest
    ): JobDetailDto {
        val job = jobEngine.getJob(id)
        val url = servletRequest.getLocationForJobRequest(job.status)

        return JobDetailDto.of(job, url, JobDetailDto.DetailType.FULL)
    }

    private fun HttpServletRequest.getLocationForJobRequest(jobStatus: JobEngine.JobStatus): String =
        "$scheme://$serverName:${localPort}$contextPath$JOBS_URI/${jobStatus.jobId}"
}
