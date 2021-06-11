package org.ionproject.integration.ui.controller

import org.ionproject.integration.JobEngine
import org.ionproject.integration.ui.dto.CreateTimetableJobDto
import org.ionproject.integration.ui.dto.InputDto
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jobs")
class JobController(
    val jobEngine: JobEngine,
    val input: InputDto
) {

    private val logger = LoggerFactory.getLogger(JobController::class.java)

    @PostMapping("/${JobEngine.TIMETABLE_JOB_NAME}", consumes = ["application/json"])
    fun createTimetableJob(@RequestBody body: CreateTimetableJobDto): String {
        val request = input.runCatching {
            getTimetableJobRequest(body)
        }.onFailure { logger.error("Failure parsing request: $body") }

        if (request.isFailure) // TODO: Implement proper error handling
            return "Error: ${request.exceptionOrNull()?.message ?: "Unknown error"}"
        val requestResult = jobEngine.runTimetableJob(request.getOrThrow())

        return when (requestResult.result) {
            JobEngine.JobExecutionResult.CREATED -> "OK: ${requestResult.jobId}"
            else -> "FAILED: ${requestResult.result}"
        }
    }

    @PostMapping("/${JobEngine.ACADEMIC_CALENDAR_JOB_NAME}", consumes = ["application/json"])
    fun createCalendarJob(@RequestBody job: String): String {
        return "Calendar OK"
    }

    @GetMapping
    fun getJobs(): String {
        return "Sum jobs yo"
    }
}
