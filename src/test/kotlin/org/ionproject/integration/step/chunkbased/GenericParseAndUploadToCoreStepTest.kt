package org.ionproject.integration.step.chunkbased

import java.io.File
import java.lang.reflect.UndeclaredThrowableException
import java.nio.file.Paths
import java.time.Instant
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.job.Generic
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        Generic::class,
        IOnIntegrationApplication::class
    ]
)
@TestPropertySource("classpath:application.properties")
internal class GenericParseAndUploadToCoreStepTest {

    @Autowired
    @Qualifier(value = "genericJob")
    private lateinit var job: Job

    @Autowired
    private lateinit var jobLauncher: JobLauncher

    @Autowired
    private lateinit var jobRepository: JobRepository

    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @BeforeEach
    private fun initializeJobLauncherTestUtils() {
        jobLauncherTestUtils = JobLauncherTestUtils()
        jobLauncherTestUtils.jobLauncher = jobLauncher
        jobLauncherTestUtils.jobRepository = jobRepository
        jobLauncherTestUtils.job = job
    }

    private fun initJobParameters(jobType: String): JobParameters {
        return JobParametersBuilder()
            .addString("alertRecipient", "org.ionproject@gmail.com")
            .addString("jobType", jobType)
            .addLong("timestamp", Instant.now().toEpochMilli())
            .toJobParameters()
    }

    @Test
    fun whenAcademicCalendarIsSuccessfullyParsed_thenAssertFileDoesNotExistAndHashIsInContext() {
        val expectedHash = byteArrayOf(
            -76, -54, 99, -47, 18, 12, -70, 35, -26, -50, -91, 124, 62, 40, 41,
            -81, 49, -82, -4, 81, 69, -103, -6, 98, 27, 114, -77, 84, -112, -117, 39, 61
        )
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased" +
                "/generic/academic-calendar/academic-calendar.yml"
        )
        val temp = File("src/test/resources/school-a.yml")
        path.toFile().copyTo(temp)
        val jp = initJobParameters("ACADEMIC_CALENDAR")
        val ec = SpringBatchTestUtils().createExecutionContext()
        ec.put("file-path", temp.toPath())

        val je = jobLauncherTestUtils.launchStep("Parse And Upload to Core Step", jp, ec)
        val actualHash = je.executionContext["file-hash"] as ByteArray

        assertFalse(temp.exists())
        assertTrue(expectedHash.contentEquals(actualHash))
    }
    @Test
    fun whenExamScheduleIsSuccessfullyParsed_thenAssertFileDoesNotExistAndHashIsInContext() {
        val expectedHash = byteArrayOf(
            -83, -72, 69, -118, -3, 115, 110, -19, 119, 20, 40, -55, 96, -40, 47, -38, -100,
            15, -102, -32, 7, 33, -90, 105, 78, 49, 124, 67, 112, -10, 15, 114
        )
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased/generic/exam-schedule/exam-schedule.yml"
        )
        val temp = File("src/test/resources/school-a.yml")
        path.toFile().copyTo(temp)
        val jp = initJobParameters("EXAM_SCHEDULE")
        val ec = SpringBatchTestUtils().createExecutionContext()
        ec.put("file-path", temp.toPath())

        val je = jobLauncherTestUtils.launchStep("Parse And Upload to Core Step", jp, ec)
        val actualHash = je.executionContext["file-hash"] as ByteArray

        assertFalse(temp.exists())
        assertTrue(expectedHash.contentEquals(actualHash))
    }
    @Test
    fun whenFileIsMismatchedWithJobType_thenThrowYamlException() {
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased/generic/exam-schedule/exam-schedule.yml"
        )
        val temp = File("src/test/resources/school-a.yml")
        path.toFile().copyTo(temp)
        val jp = initJobParameters("ACADEMIC_CALENDAR")
        val ec = SpringBatchTestUtils().createExecutionContext()
        ec.put("file-path", temp.toPath())

        val je = jobLauncherTestUtils.launchStep("Parse And Upload to Core Step", jp, ec)
        val actualHash = je.executionContext["file-hash"]
        val ex = je.allFailureExceptions[0] as UndeclaredThrowableException

        assertNull(actualHash)
        assertFalse(temp.exists())
        assertEquals("YAMLException", ex.undeclaredThrowable::class.java.simpleName)
        assertEquals("Invalid yaml", ex.undeclaredThrowable.message)
    }
    @Test
    fun whenJobTypeDoesNotExist_thenThrowIllegaArgumentException() {
        val path = Paths.get(
            "src/test/resources/org/ionproject/integration/step/chunkbased" +
                "/generic/academic-calendar/academic-calendar.yml"
        )
        val temp = File("src/test/resources/school-a.yml")
        path.toFile().copyTo(temp)
        val jp = initJobParameters("DUMMY")
        val ec = SpringBatchTestUtils().createExecutionContext()
        ec.put("file-path", temp.toPath())

        val je = jobLauncherTestUtils.launchStep("Parse And Upload to Core Step", jp, ec)
        val exceptions = je.allFailureExceptions

        assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        assertEquals("IllegalArgumentException", exceptions[0]::class.java.simpleName)
        assertEquals(
            "No enum constant org.ionproject.integration.model.internal.generic.JobType.DUMMY",
            exceptions[0].message
        )
        assertFalse(temp.exists())
        assertNull(je.executionContext["file-hash"])
    }

    @Test
    fun whenPathDoesNotExist_thenThrowYamlException() {
        val temp = File("src/test/resources/dummy")
        val jp = initJobParameters("ACADEMIC_CALENDAR")
        val ec = SpringBatchTestUtils().createExecutionContext()
        ec.put("file-path", temp.toPath())

        val je = jobLauncherTestUtils.launchStep("Parse And Upload to Core Step", jp, ec)
        val exceptions = je.allFailureExceptions[0] as UndeclaredThrowableException

        assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        assertEquals("YAMLException", exceptions.undeclaredThrowable::class.java.simpleName)
        assertEquals(
            "File src/test/resources/dummy does not exist or is a directory.",
            exceptions.undeclaredThrowable.message
        )
        assertFalse(temp.exists())
        assertNull(je.executionContext["file-hash"])
    }
}
