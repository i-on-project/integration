package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.external.timetable.TimetableTeachers
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.test.MetaDataInstanceFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.test.context.TestPropertySource
import java.io.File
import javax.mail.internet.MimeMessage

internal class WriteFileTaskletTestFixtures {
    companion object {
        val timetableTeachers =
            TimetableTeachers(
                timetable = listOf(
                    Timetable(school = School(name = "timetable")),
                    Timetable(school = School(name = "timetable"))
                ),
                teachers = listOf(
                    CourseTeacher(
                        school = School(
                            name = "courseTeacher"
                        )
                    ),
                    CourseTeacher(
                        school = School(
                            name = "courseTeacher"
                        )
                    )
                )
            )
    }
}

@RunWith(MockitoJUnitRunner::class)
@SpringBootTest
@TestPropertySource(
    "classpath:application.properties",
    properties = [
        "ion.core-retries = 3"
    ]
)
class WriteFileTaskletTests {
    @Autowired
    private lateinit var appProperties: AppProperties

    @Autowired
    private lateinit var state: ISELTimetable.State

    @Mock
    private lateinit var sender: JavaMailSenderImpl

    @Mock
    private lateinit var mimeMessage: MimeMessage

    private lateinit var writeFileTasklet: WriteFileTasklet

    private lateinit var stepContribution: StepContribution
    private lateinit var chunkContext: ChunkContext

    @BeforeEach
    fun setUp() {
        stepContribution = StepContribution(
            StepExecution(
                "WriteFileTaskletTests",
                MetaDataInstanceFactory.createJobExecution()
            )
        )
        chunkContext = SpringBatchTestUtils().createChunkContext()

        Mockito
            .`when`(chunkContext.stepContext.jobParameters)
            .thenReturn(
                mapOf(
                    "srcRemoteLocation" to "https://www.isel.pt/media/uploads/LEIC_0310.pdf",
                    "alertRecipient" to "client@domain.com"
                )
            )

        Mockito
            .`when`(sender.createMimeMessage())
            .thenReturn(mimeMessage)

        state.timetableTeachers = WriteFileTaskletTestFixtures.timetableTeachers
        writeFileTasklet = WriteFileTasklet(state)
    }

    @Test
    fun whenANew_thenWriteFileToDisk_andConfirmFileExists() {
        val localFilePath = "src/test/resources/timetable.json"
        val file = File(localFilePath)
        try {
            file.writeText(
                JsonUtils.toJson(state.timetableTeachers.timetable[0]).orThrow()
            )
            assertTrue(file.exists())
        } finally {
            file.deleteOnExit()
        }
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
}
