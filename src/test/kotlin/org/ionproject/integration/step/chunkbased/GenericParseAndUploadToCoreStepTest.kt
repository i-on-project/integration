package org.ionproject.integration.step.chunkbased

import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.job.Generic
import org.ionproject.integration.step.chunkbased.writer.GenericCoreWriter
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File
import java.lang.reflect.UndeclaredThrowableException
import java.nio.file.Paths
import java.time.Instant

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

    @MockBean
    @Qualifier("GenericCoreWriter")
    private lateinit var writer: GenericCoreWriter

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

    /* Calculating file hashes in windows yields a different result than in unix/linux.
    * the value for expected hash in windows is:
    *  byteArrayOf(-112, -126, -94, 59, -4, -97, 89, 44, 107, -8, 21,
                -34, -57, -3, -117, 25, 84, -2, 107, -89, -6, -8, -23, -64, 61, 89, 7, -76, -124, -92, -44, 58)
    * */
    @Test
    fun whenAcademicCalendarIsSuccessfullyParsed_thenAssertFileDoesNotExistAndHashIsInContext() {
        val expectedHash = byteArrayOf(
                96, 69, -9, 111, -77, 28, 84, 84, -5, -51, 32, 2, -29, -125, 107, -91, 10,
                10, 101, -96, -99, -115, 35, 114, -88, 108, 111, 123, 27, 85, -47, 38
        )

        val path = Paths.get(
                "src/test/resources/org/ionproject/integration/step/chunkbased" +
                        "/generic/academic-calendar/academic-calendar.yml".replace("/", File.separator)
        )
        val temp = File("src/test/resources/school-a.yml".replace("/", File.separator))
        path.toFile().copyTo(temp)
        val jp = initJobParameters("ACADEMIC_CALENDAR")
        val ec = SpringBatchTestUtils().createExecutionContext()
        ec.put("file-path", temp.toPath())

        val je = jobLauncherTestUtils.launchStep("Parse And Upload to Core Step", jp, ec)
        val actualHash = je.executionContext["file-hash"] as ByteArray

        assertFalse(temp.exists())
        actualHash.forEach { print("$it, ") }
        assertTrue(expectedHash.contentEquals(actualHash))
        Mockito.verify(writer, times(1)).write(Mockito.anyList())
    }

    /*Calculating file hashes in windows yields a different result than in unix/linux.
    * the value for expected hash in windows is:
    * byteArrayOf(85, 47, 92, -18, -57, 124, -33, 31, 83, 31, 122, 4, 103, 21, -113, -61, -89,
                -92, -94, -24, -22, 105, -120, 121, -28, -103, -3, 81, -48, -61, -79, -10)
     */
    @Test
    fun whenExamScheduleIsSuccessfullyParsed_thenAssertFileDoesNotExistAndHashIsInContext() {
        val expectedHash = byteArrayOf(
                -55, -84, 127, -43, 94, -70, -94, 86, -116, -32, -95, 75, -109, -111, 82, 93,
                98, 113, -10, 37, -30, -117, 64, 2, -2, 40, 107, -121, 126, 53, -55, 81
        )

        val path = Paths.get(
                "src/test/resources/org/ionproject/integration/step/chunkbased/generic/exam-schedule/exam-schedule.yml".replace("/", File.separator)
        )
        val temp = File("src/test/resources/school-a.yml".replace("/", File.separator))
        path.toFile().copyTo(temp)
        val jp = initJobParameters("EXAM_SCHEDULE")
        val ec = SpringBatchTestUtils().createExecutionContext()
        ec.put("file-path", temp.toPath())

        val je = jobLauncherTestUtils.launchStep("Parse And Upload to Core Step", jp, ec)
        val actualHash = je.executionContext["file-hash"] as ByteArray

        assertFalse(temp.exists())
        actualHash.forEach { print("$it, ") }
        assertTrue(expectedHash.contentEquals(actualHash))
        Mockito.verify(writer, times(1)).write(Mockito.anyList())
    }

    @Test
    fun whenFileIsMismatchedWithJobType_thenThrowYamlException() {
        val path = Paths.get(
                "src/test/resources/org/ionproject/integration/step/chunkbased/generic/exam-schedule/exam-schedule.yml".replace("/", File.separator)
        )
        val temp = File("src/test/resources/school-a.yml".replace("/", File.separator))
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
        Mockito.verify(writer, times(0)).write(Mockito.anyList())
    }

    @Test
    fun whenJobTypeDoesNotExist_thenThrowIllegaArgumentException() {
        val path = Paths.get(
                "src/test/resources/org/ionproject/integration/step/chunkbased" +
                        "/generic/academic-calendar/academic-calendar.yml".replace("/", File.separator)
        )
        val temp = File("src/test/resources/school-a.yml".replace("/", File.separator))
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
        Mockito.verify(writer, times(0)).write(Mockito.anyList())
    }

    @Test
    fun whenPathDoesNotExist_thenThrowYamlException() {
        val temp = File("src/test/resources/dummy".replace("/", File.separator))
        val jp = initJobParameters("ACADEMIC_CALENDAR")
        val ec = SpringBatchTestUtils().createExecutionContext()
        ec.put("file-path", temp.toPath())

        val je = jobLauncherTestUtils.launchStep("Parse And Upload to Core Step", jp, ec)
        val exceptions = je.allFailureExceptions[0] as UndeclaredThrowableException

        assertEquals(ExitStatus.FAILED.exitCode, je.exitStatus.exitCode)
        assertEquals("YAMLException", exceptions.undeclaredThrowable::class.java.simpleName)
        assertEquals(
                "File ${temp.path} does not exist or is a directory.",
                exceptions.undeclaredThrowable.message
        )
        assertFalse(temp.exists())
        assertNull(je.executionContext["file-hash"])
        Mockito.verify(writer, times(0)).write(Mockito.anyList())
    }
}
