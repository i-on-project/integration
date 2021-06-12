package org.ionproject.integration.ui.controller

import org.ionproject.integration.JobEngine
import org.ionproject.integration.ui.dto.CreateCalendarJobDto
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
        val request = input.getTimetableJobRequest(body)
        val requestResult = jobEngine.runTimetableJob(request)

        return when (requestResult.result) {
            JobEngine.JobExecutionResult.CREATED -> "OK: ${requestResult.jobId}"
            else -> "FAILED: ${requestResult.result}"
        }
    }

    @PostMapping("/${JobEngine.CALENDAR_JOB_NAME}", consumes = ["application/json"])
    fun createCalendarJob(@RequestBody body: CreateCalendarJobDto): String {
        val request = input.getCalendarJobRequest(body)
        val requestResult = jobEngine.runCalendarJob(request)

        return when (requestResult.result) {
            JobEngine.JobExecutionResult.CREATED -> "OK: ${requestResult.jobId}"
            else -> "FAILED: ${requestResult.result}"
        }
    }

    @GetMapping
    fun getJobs(): String {
        return "Sum jobs yo"
    }
}
