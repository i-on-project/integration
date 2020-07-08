package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import javax.mail.internet.MimeMessage
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.timetable.CourseTeacher
import org.ionproject.integration.model.internal.timetable.School
import org.ionproject.integration.model.internal.timetable.Timetable
import org.ionproject.integration.model.internal.timetable.TimetableTeachers
import org.ionproject.integration.model.internal.timetable.UploadType
import org.ionproject.integration.service.implementations.CoreService
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.ionproject.integration.utils.Try
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.batch.test.MetaDataInstanceFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.test.context.TestPropertySource

internal class UploadTaskletTestFixtures {
    companion object {
        val timetableTeachers = TimetableTeachers(
            timetable = listOf(
                Timetable(school = School(name = "timetable")),
                Timetable(school = School(name = "timetable"))
            ),
            teachers = listOf(
                CourseTeacher(school = School(name = "courseTeacher")),
                CourseTeacher(school = School(name = "courseTeacher"))
            )
        )
    }
}

@RunWith(MockitoJUnitRunner::class)
@SpringBootTest
@TestPropertySource("classpath:application.properties",
    properties = [
        "ion.core-retries = 3"
    ]
)
class UploadTaskletTests {
    @Autowired
    private lateinit var appProperties: AppProperties

    @Autowired
    private lateinit var state: ISELTimetable.State

    @Mock
    private lateinit var coreService: CoreService

    @Mock
    private lateinit var sender: JavaMailSenderImpl

    @Mock
    private lateinit var mimeMessage: MimeMessage

    private lateinit var uploadTasklet: UploadTasklet

    private lateinit var stepContribution: StepContribution
    private lateinit var chunkContext: ChunkContext

    @BeforeEach
    fun setUp() {
        stepContribution = StepContribution(StepExecution("UploadTaskletTests",
            MetaDataInstanceFactory.createJobExecution()))
        chunkContext = SpringBatchTestUtils().createChunkContext()

        Mockito
            .`when`(chunkContext.stepContext.jobParameters)
            .thenReturn(mapOf(
                "pdfRemoteLocation" to "https://www.isel.pt/media/uploads/LEIC_0310.pdf",
                "alertRecipient" to "client@domain.com"
            ))

        Mockito
            .`when`(sender.createMimeMessage())
            .thenReturn(mimeMessage)

        state.timetableTeachers = UploadTaskletTestFixtures.timetableTeachers
        uploadTasklet = UploadTasklet(coreService, appProperties, state, sender)
    }

    private fun contextContainsKey(context: ChunkContext, key: String) = context
        .stepContext
        .stepExecution
        .jobExecution
        .executionContext
        .containsKey(key)

    private fun contextGetInt(context: ChunkContext, key: String) = context
        .stepContext
        .stepExecution
        .jobExecution
        .executionContext
        .getInt(key)

    @Test
    fun whenUploadTimetableSuccessful_thenRepeatStatusFinished_andCoreRetriesDoesntExist() {
        // Arrange
        uploadTasklet.setUploadType(UploadType.TIMETABLE)

        Mockito
            .`when`(coreService.pushTimetable(ArgumentMatchers.anyList()))
            .thenReturn(Try.ofValue(CoreResult.SUCCESS))

        // Act
        val result = uploadTasklet.execute(stepContribution, chunkContext)

        // Assert
        assertEquals(RepeatStatus.FINISHED, result)
        assertFalse(contextContainsKey(chunkContext, "CoreRetries"))
    }

    @Test
    fun whenUploadTeachersSuccessful_thenRepeatStatusFinished_andCoreRetriesDoesntExist() {
        // Arrange
        uploadTasklet.setUploadType(UploadType.TEACHERS)

        Mockito
            .`when`(coreService.pushCourseTeacher(ArgumentMatchers.anyList()))
            .thenReturn(Try.ofValue(CoreResult.SUCCESS))

        // Act
        val result = uploadTasklet.execute(stepContribution, chunkContext)

        // Assert
        assertEquals(RepeatStatus.FINISHED, result)
        assertFalse(contextContainsKey(chunkContext, "CoreRetries"))
    }

    @Test
    fun whenUploadTimetableTryAgain_thenRepeatStatusFinished_andCoreRetriesIsZero() {
        // Arrange
        uploadTasklet.setUploadType(UploadType.TIMETABLE)

        Mockito
            .`when`(coreService.pushTimetable(ArgumentMatchers.anyList()))
            .thenReturn(Try.ofValue(CoreResult.TRY_AGAIN))

        // Act
        val result = uploadTasklet.execute(stepContribution, chunkContext)
        val firstRetry = uploadTasklet.execute(stepContribution, chunkContext)
        val secondRetry = uploadTasklet.execute(stepContribution, chunkContext)

        // Assert
        assertEquals(RepeatStatus.CONTINUABLE, result)
        assertEquals(RepeatStatus.CONTINUABLE, firstRetry)
        assertEquals(RepeatStatus.FINISHED, secondRetry)
        assertTrue(contextContainsKey(chunkContext, "CoreRetries"))
        assertEquals(0, contextGetInt(chunkContext, "CoreRetries"))
        verify(sender, times(1)).send(any(MimeMessage::class.java))
    }

    @Test
    fun whenUploadTeachersTryAgain_thenRepeatStatusFinished_andCoreRetriesIsZero() {
        // Arrange
        uploadTasklet.setUploadType(UploadType.TEACHERS)

        Mockito
            .`when`(coreService.pushCourseTeacher(ArgumentMatchers.anyList()))
            .thenReturn(Try.ofValue(CoreResult.TRY_AGAIN))

        // Act
        val result = uploadTasklet.execute(stepContribution, chunkContext)
        val firstRetry = uploadTasklet.execute(stepContribution, chunkContext)
        val secondRetry = uploadTasklet.execute(stepContribution, chunkContext)

        // Assert
        assertEquals(RepeatStatus.CONTINUABLE, result)
        assertEquals(RepeatStatus.CONTINUABLE, firstRetry)
        assertEquals(RepeatStatus.FINISHED, secondRetry)
        assertTrue(contextContainsKey(chunkContext, "CoreRetries"))
        assertEquals(0, contextGetInt(chunkContext, "CoreRetries"))
        verify(sender, times(1)).send(any(MimeMessage::class.java))
    }

    @Test
    fun whenUploadTimetableUnrecoverableError_thenRepeatStatusFinished_andCoreRetriesDoesntExist() {
        // Arrange
        uploadTasklet.setUploadType(UploadType.TIMETABLE)

        Mockito
            .`when`(coreService.pushTimetable(ArgumentMatchers.anyList()))
            .thenReturn(Try.ofValue(CoreResult.UNRECOVERABLE_ERROR))

        // Act
        val result = uploadTasklet.execute(stepContribution, chunkContext)

        // Assert
        assertEquals(RepeatStatus.FINISHED, result)
        assertFalse(contextContainsKey(chunkContext, "CoreRetries"))
        verify(sender, times(1)).send(any(MimeMessage::class.java))
    }

    @Test
    fun whenUploadTeachersUnrecoverableError_thenRepeatStatusFinished_andCoreRetriesDoesntExist() {
        // Arrange
        uploadTasklet.setUploadType(UploadType.TEACHERS)

        Mockito
            .`when`(coreService.pushCourseTeacher(ArgumentMatchers.anyList()))
            .thenReturn(Try.ofValue(CoreResult.UNRECOVERABLE_ERROR))

        // Act
        val result = uploadTasklet.execute(stepContribution, chunkContext)

        // Assert
        assertEquals(RepeatStatus.FINISHED, result)
        assertFalse(contextContainsKey(chunkContext, "CoreRetries"))
        verify(sender, times(1)).send(any(MimeMessage::class.java))
    }
}
