package org.ionproject.integration.ui.controller

import org.hamcrest.CoreMatchers.containsString
import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.application.job.JobType
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.ionproject.integration.ui.dto.InputProcessor
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.URI
import java.time.LocalDateTime

@WebMvcTest
class JobControllerTests {
    @MockBean
    private lateinit var jobEngine: JobEngine

    @MockBean
    private lateinit var inputProcessor: InputProcessor

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val mockInstitution = InstitutionModel("test", "test", "test", URI("www.test.com"))

    @Test
    fun `when receiving a request to list running jobs then return OK`() {
        val date = LocalDateTime.of(2020, 6, 30, 15, 3)

        val jobParams = JobEngine.IntegrationJobParameters(
            creationDate = date,
            startDate = date,
            format = OutputFormat.YAML,
            institution = mockInstitution,
            uri = URI("www.test.com")
        )

        val mockJob1 = JobEngine.IntegrationJob(
            type = JobType.TIMETABLE,
            status = JobEngine.JobStatus(1, JobEngine.JobExecutionResult.RUNNING),
            parameters = jobParams
        )

        val mockJob2 = JobEngine.IntegrationJob(
            type = JobType.ACADEMIC_CALENDAR,
            status = JobEngine.JobStatus(3, JobEngine.JobExecutionResult.CREATED),
            parameters = jobParams
        )

        val expectedResponse =
            """[{"type":"TIMETABLE","status":{"jobId":1,"result":"RUNNING"},"parameters":{"creationDate":"2020-06-30T15:03:00","startDate":"2020-06-30T15:03:00","format":"YAML","institution":{"name":"test","acronym":"test","identifier":"test","academicCalendarUri":"www.test.com"},"programme":null,"uri":"www.test.com"}},{"type":"ACADEMIC_CALENDAR","status":{"jobId":3,"result":"CREATED"},"parameters":{"creationDate":"2020-06-30T15:03:00","startDate":"2020-06-30T15:03:00","format":"YAML","institution":{"name":"test","acronym":"test","identifier":"test","academicCalendarUri":"www.test.com"},"programme":null,"uri":"www.test.com"}}]"""

        whenever(jobEngine.getRunningJobs()) doReturn listOf(mockJob1, mockJob2)

        mockMvc.perform(get(JOBS_URI))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString(expectedResponse)))
    }

    @Test
    fun `when receiving a run job request then return CREATED`() {

        whenever(inputProcessor.getJobRequest(any())) doReturn JobEngine.CalendarJobRequest(
            OutputFormat.YAML,
            mockInstitution
        )
        whenever(jobEngine.runJob(any())) doReturn JobEngine.JobStatus(1, JobEngine.JobExecutionResult.CREATED)

        val expectedResponse = "Created CalendarJobRequest job with ID 1"

        mockMvc.perform(
            post(JOBS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", """localhost:80/jobs/1"""))
            .andExpect(content().string(containsString(expectedResponse)))
    }

    @Test
    fun `when receiving a bad job request then return BAD_REQUEST`() {
        whenever(inputProcessor.getJobRequest(any())) doThrow ArgumentException("You've been a bad, bad boy!")

        val expectedResponse = "You've been a bad, bad boy!"

        mockMvc.perform(
            post(JOBS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString(expectedResponse)))
    }
}
