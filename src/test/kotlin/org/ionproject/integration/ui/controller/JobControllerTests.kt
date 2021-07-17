package org.ionproject.integration.ui.controller

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.containsString
import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.application.job.JobType
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.ionproject.integration.ui.input.InputProcessor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.URI
import java.time.LocalDateTime

@WebMvcTest
@TestPropertySource("classpath:application.properties")
class JobControllerTests {
    @MockBean
    private lateinit var jobEngine: JobEngine

    @MockBean
    private lateinit var inputProcessor: InputProcessor

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Value("\${server.servlet.context-path}")
    private lateinit var contextPath: String

    private val mockInstitution = InstitutionModel("test", "test", "test", "Europe/Lisbon", URI("www.test.com"))

    @BeforeEach
    fun setUp() {
        assertThat(contextPath).isNotBlank
        (mockMvc.dispatcherServlet.servletContext as MockServletContext).contextPath = contextPath
    }

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
            """[{"type":"timetable","id":1,"status":"RUNNING","createdOn":"2020-06-30T15:03:00Z","startedOn":"2020-06-30T15:03:00Z","links":{"self":"http://localhost/integration/jobs/1"}},{"type":"calendar","id":3,"status":"CREATED","createdOn":"2020-06-30T15:03:00Z","startedOn":"2020-06-30T15:03:00Z","links":{"self":"http://localhost/integration/jobs/3"}}]"""

        whenever(jobEngine.getRunningJobs()) doReturn listOf(mockJob1, mockJob2)

        mockMvc.perform(get("$contextPath$JOBS_URI").contextPath(contextPath))
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

        val expectedResponse = """{"location":"http://localhost/integration/jobs/1","status":"CREATED"}"""

        mockMvc.perform(
            post("$contextPath$JOBS_URI").contextPath(contextPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", """http://localhost$contextPath$JOBS_URI/1"""))
            .andExpect(content().string(containsString(expectedResponse)))
    }

    @Test
    fun `when receiving a bad job request then return BAD_REQUEST`() {
        whenever(inputProcessor.getJobRequest(any())) doThrow ArgumentException("You've been a bad, bad boy!")

        val expectedResponse =
            """{"type":"https://github.com/i-on-project/integration/blob/master/docs/infrastructure/ArgumentException.md","title":"Bad Request","status":400,"detail":"You've been a bad, bad boy!","instance":"/integration/jobs""""

        mockMvc.perform(
            post("$contextPath$JOBS_URI").contextPath(contextPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(content().string(containsString(expectedResponse)))
    }
}
