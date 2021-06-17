package org.ionproject.integration.ui.controller

import org.ionproject.integration.JobEngine
import org.ionproject.integration.ui.dto.CreateJobDto
import org.ionproject.integration.ui.dto.InputProcessor
import org.slf4j.LoggerFactory
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jobs")
class JobController(
    val jobEngine: JobEngine,
    val inputProcessor: InputProcessor,
    val jobExplorer: JobExplorer
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
    fun getJobs(): List<String> {
        val jobNames = jobExplorer.jobNames

        // val instances = jobExplorer.getJobInstances(TIMETABLE_JOB_NAME, 0, 1000)
        val executions = jobNames.map { jobExplorer.findRunningJobExecutions(it) }

        return executions.flatMap { it.map { je -> "${je.jobInstance.jobName} -> ${je.jobId}" } }
    }
}
