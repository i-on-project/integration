package org.ionproject.integration.ui.controller

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.ui.dto.CreateJobDto
import org.ionproject.integration.ui.dto.InputProcessor
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jobs")
class JobController(
    val jobEngine: JobEngine,
    val inputProcessor: InputProcessor,
) {

    private val logger = LoggerFactory.getLogger(JobController::class.java)

    @PostMapping(consumes = ["application/json"])
    fun createTimetableJob(@RequestBody body: CreateJobDto): String {
        val request = inputProcessor.getJobRequest(body)
        val requestResult = jobEngine.runJob(request)

        return when (requestResult.result) {
            JobEngine.JobExecutionResult.CREATED -> "Created ${request.javaClass.simpleName} job with ID ${requestResult.jobId}"
            else -> "FAILED: ${requestResult.result}"
        }
    }

    @GetMapping
    fun getJobs(): List<JobEngine.IntegrationJob> {
        val jobs = jobEngine.getRunningJobs()
        return jobs
    }

    @GetMapping("/{id}")
    fun getJobDetails(@PathVariable id: Long): JobEngine.IntegrationJob {
        val job = jobEngine.getJob(id)
        return job
    }
}
