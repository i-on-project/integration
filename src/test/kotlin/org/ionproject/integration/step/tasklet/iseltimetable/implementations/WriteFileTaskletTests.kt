package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.dispatcher.DispatchResult
import org.ionproject.integration.dispatcher.DispatcherImpl
import org.ionproject.integration.dispatcher.TimetableFileWriter
import org.ionproject.integration.dispatcher.git.GitOutcome
import org.ionproject.integration.dispatcher.git.IGitHandler
import org.ionproject.integration.dispatcher.git.IGitHandlerFactory
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.ProgrammeDto
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.external.timetable.SchoolDto
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.external.timetable.TimetableDto
import org.ionproject.integration.model.external.timetable.TimetableTeachers
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.batch.test.MetaDataInstanceFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

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

class WriteFileTaskletTests {
    @Autowired
    private lateinit var state: ISELTimetable.State

    private lateinit var writeFileTasklet: WriteFileTasklet

    private lateinit var stepContribution: StepContribution
    private lateinit var chunkContext: ChunkContext

    private lateinit var timetableDto: TimetableDto

    private val mockHandlerSuccess = mock<IGitHandler> {
        on { update() } doReturn GitOutcome.SUCCESS
    }

    private val mockHandlerFailure = mock<IGitHandler> {
        on { update() } doReturn GitOutcome.CONFLICT
    }

    private fun getMockFactory(isWorking: Boolean): IGitHandlerFactory = mock {
        on { checkout(any(), any()) } doReturn if (isWorking) mockHandlerSuccess else mockHandlerFailure
    }

    private val mockWriter = mock<TimetableFileWriter> {
        on { write(any(), any()) } doReturn File("")
    }

    private val appProps = mock<AppProperties> {
        on { gitBranchName } doReturn ""
        on { gitRepository } doReturn ""
        on { gitRepoUrl } doReturn ""
        on { gitUser } doReturn ""
        on { gitPassword } doReturn ""
        on { stagingDir } doReturn ""
    }

    private val dispatcher = DispatcherImpl(mockWriter, getMockFactory(true)).apply { props = appProps }

    @BeforeEach
    fun setUp() {
        timetableDto = TimetableDto(
            "20210421T204916Z",
            "20210421T204916Z",
            SchoolDto(
                "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
                "ISEL"
            ),
            ProgrammeDto(
                "Licenciatura em Engenharia Informática e de Computadores",
                "LEIC"
            ),
            "2020-2021-1",
            listOf()
        )

        stepContribution = StepContribution(
            StepExecution(
                "WriteFileTaskletTests",
                MetaDataInstanceFactory.createJobExecution()
            )
        )
        chunkContext = SpringBatchTestUtils().createChunkContext()

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

    @Test
    fun `when dispatcher returns success then execute tasklet execution ends`() {
        whenever(writeFileTasklet.writeToGit()).thenReturn(DispatchResult.SUCCESS)

        assertEquals(RepeatStatus.FINISHED, writeFileTasklet.execute(stepContribution, chunkContext))
    }

    @Test
    fun `when dispatcher returns failure then execute tasklet execution ends`() {
        whenever(writeFileTasklet.writeToGit()).thenReturn(DispatchResult.FAILURE)

        assertEquals(RepeatStatus.FINISHED, writeFileTasklet.execute(stepContribution, chunkContext))
    }

    @Test
    fun `when calendar term is not expected then execute fail`() {

        val badTimetableDto = TimetableDto(
            "20210421T204916Z",
            "20210421T204916Z",
            SchoolDto(
                "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
                "ISEL"
            ),
            ProgrammeDto(
                "Licenciatura em Engenharia Informática e de Computadores",
                "LEIC"
            ),
            "2020-2021-4",
            listOf()
        )

        assertThrows<IllegalArgumentException> {
            writeFileTasklet.generateTimetableDataFromDto(badTimetableDto)
        }
    }
}
