package org.ionproject.integration.ui.dto

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.application.job.JobType
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.file.INVALID_FORMAT_ERROR
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.ionproject.integration.infrastructure.repository.IInstitutionRepository
import org.ionproject.integration.infrastructure.repository.IProgrammeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.net.URI

private const val TEST_SCHOOL_NAME = "Test School"
private const val TEST_SCHOOL_ACRONYM = "TS"
private const val TEST_PROGRAMME_ACRONYM = "SP"
private const val TEST_PROGRAMME_NAME = "Some Engineering Programme I Guess"
private const val TEST_SCHOOL_ID = "test.school"
private val TEST_URI = URI("test.school./someFile.pdf")

class InputProcessorTests {
    private val testInstitution = InstitutionModel(
        TEST_SCHOOL_NAME,
        TEST_SCHOOL_ACRONYM,
        TEST_SCHOOL_ID,
        TEST_URI
    )

    private val testProgramme = ProgrammeModel(
        institutionModel = testInstitution,
        name = TEST_PROGRAMME_NAME,
        acronym = TEST_PROGRAMME_ACRONYM,
        timetableUri = TEST_URI
    )

    private val mockInstitutionRepoOK = mock<IInstitutionRepository> {
        on { getInstitutionByIdentifier(any()) } doReturn testInstitution
    }
    private val mockProgrammeRepoOK = mock<IProgrammeRepository> {
        on { getProgrammeByAcronymAndInstitution(any(), any()) } doReturn testProgramme
    }

    private val inputProcessor = InputProcessor(mockInstitutionRepoOK, mockProgrammeRepoOK)

    @Test
    fun `when given a valid CALENDAR job then return job request OK`() {
        val createCalendarJob = CreateJobDto(
            institution = TEST_SCHOOL_ID,
            format = OutputFormat.JSON.name,
            type = JobType.ACADEMIC_CALENDAR.identifier
        )

        val expected = JobEngine.CalendarJobRequest(
            format = OutputFormat.JSON,
            institution = testInstitution,
        )
        val actual = inputProcessor.getJobRequest(createCalendarJob)

        assertEquals(expected, actual)
    }

    @Test
    fun `when given a valid job with extra whitespace then trim and return job request OK`() {
        val createCalendarJob = CreateJobDto(
            institution = " $TEST_SCHOOL_ID   ",
            format = "    ${OutputFormat.JSON.name}  ",
            type = "   ${JobType.ACADEMIC_CALENDAR.identifier} "
        )

        val expected = JobEngine.CalendarJobRequest(
            format = OutputFormat.JSON,
            institution = testInstitution,
        )
        val actual = inputProcessor.getJobRequest(createCalendarJob)

        assertEquals(expected, actual)
    }

    @Test
    fun `when given a valid TIMETABLE job then return job request OK`() {
        val createTimetableJob = CreateJobDto(
            institution = TEST_SCHOOL_ID,
            programme = TEST_PROGRAMME_ACRONYM,
            format = OutputFormat.YAML.name,
            type = JobType.TIMETABLE.identifier
        )

        val expected = JobEngine.TimetableJobRequest(
            format = OutputFormat.YAML,
            institution = testInstitution,
            programme = testProgramme
        )
        val actual = inputProcessor.getJobRequest(createTimetableJob)

        assertEquals(expected, actual)
    }

    @Test
    fun `when given an empty job then throw exception`() {
        val expected = INVALID_JOB_TYPE_ERROR.format("null")

        val ex = assertThrows<ArgumentException> {
            inputProcessor.getJobRequest(CreateJobDto())
        }

        assertEquals(expected, ex.message)
    }

    @Test
    fun `when given job with empty format then throw exception`() {
        val expected = MISSING_PARAMETER_ERROR.format(FORMAT)

        val ex = assertThrows<ArgumentException> {
            val jobDto = CreateJobDto(
                institution = "ok",
                format = " ",
                type = JobType.ACADEMIC_CALENDAR.identifier
            )
            inputProcessor.getJobRequest(jobDto)
        }

        assertEquals(expected, ex.message)
    }

    @Test
    fun `when given job with invalid format then throw exception`() {
        val expected = INVALID_FORMAT_ERROR.format("jason")

        val ex = assertThrows<ArgumentException> {
            val jobDto = CreateJobDto(
                institution = "ok",
                format = "   jason",
                type = JobType.ACADEMIC_CALENDAR.identifier
            )
            inputProcessor.getJobRequest(jobDto)
        }

        assertEquals(expected, ex.message)
    }

    @Test
    fun `when given job with null format then throw exception`() {
        val expected = MISSING_PARAMETER_ERROR.format(FORMAT)

        val ex = assertThrows<ArgumentException> {
            val jobDto = CreateJobDto(
                institution = "ok",
                type = JobType.ACADEMIC_CALENDAR.identifier
            )
            inputProcessor.getJobRequest(jobDto)
        }

        assertEquals(expected, ex.message)
    }

    @Test
    fun `when given job with empty institution then throw exception`() {
        val expected = MISSING_PARAMETER_ERROR.format(INSTITUTION)

        val ex = assertThrows<ArgumentException> {
            val jobDto = CreateJobDto(
                institution = " ",
                format = OutputFormat.YAML.name,
                type = JobType.ACADEMIC_CALENDAR.identifier
            )
            inputProcessor.getJobRequest(jobDto)
        }

        assertEquals(expected, ex.message)
    }

    @Test
    fun `when given job with null institution then throw exception`() {
        val expected = MISSING_PARAMETER_ERROR.format(INSTITUTION)

        val ex = assertThrows<ArgumentException> {
            val jobDto = CreateJobDto(
                format = OutputFormat.YAML.name,
                type = JobType.ACADEMIC_CALENDAR.identifier
            )
            inputProcessor.getJobRequest(jobDto)
        }

        assertEquals(expected, ex.message)
    }

    @Test
    fun `when given job with empty programme then throw exception`() {
        val expected = MISSING_PARAMETER_ERROR.format(PROGRAMME)

        val ex = assertThrows<ArgumentException> {
            val jobDto = CreateJobDto(
                institution = TEST_SCHOOL_ID,
                programme = "          ",
                format = OutputFormat.YAML.name,
                type = JobType.TIMETABLE.identifier
            )
            inputProcessor.getJobRequest(jobDto)
        }

        assertEquals(expected, ex.message)
    }

    @Test
    fun `when given job with null programme then throw exception`() {
        val expected = MISSING_PARAMETER_ERROR.format(PROGRAMME)

        val ex = assertThrows<ArgumentException> {
            val jobDto = CreateJobDto(
                institution = TEST_SCHOOL_ID,
                format = OutputFormat.YAML.name,
                type = JobType.TIMETABLE.identifier
            )
            inputProcessor.getJobRequest(jobDto)
        }

        assertEquals(expected, ex.message)
    }

    @Test
    fun `when given an empty job type then throw exception`() {
        val expected = INVALID_JOB_TYPE_ERROR.format("null")

        val ex = assertThrows<ArgumentException> {
            inputProcessor.getJobRequest(
                CreateJobDto(" ")
            )
        }

        assertEquals(expected, ex.message)
    }

    @Test
    fun `when given an invalid job type then throw exception`() {
        val jobType = "  invalid_job "
        val expected = INVALID_JOB_TYPE_ERROR.format(jobType.trim())

        val ex = assertThrows<ArgumentException> {
            inputProcessor.getJobRequest(CreateJobDto(type = jobType))
        }

        assertEquals(expected, ex.message)
    }
}
