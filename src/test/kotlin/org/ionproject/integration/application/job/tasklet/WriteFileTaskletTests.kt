package org.ionproject.integration.application.job.tasklet

import org.ionproject.integration.application.dispatcher.DispatchResult
import org.ionproject.integration.application.dispatcher.IDispatcher
import org.ionproject.integration.application.job.ISELTimetableJob
import org.ionproject.integration.domain.common.Programme
import org.ionproject.integration.domain.common.dto.ProgrammeDto
import org.ionproject.integration.domain.common.School
import org.ionproject.integration.domain.common.dto.SchoolDto
import org.ionproject.integration.domain.timetable.Timetable
import org.ionproject.integration.domain.timetable.dto.TimetableDto
import org.ionproject.integration.domain.timetable.TimetableTeachers
import org.ionproject.integration.application.job.chunkbased.SpringBatchTestUtils
import org.ionproject.integration.infrastructure.text.JsonUtils
import org.ionproject.integration.infrastructure.orThrow
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
        val timetableTeachers = TimetableTeachers(
            listOf(
                Timetable(
                    "20210421T204916Z",
                    "20210421T204916Z",
                    School(
                        "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
                        "ISEL"
                    ),
                    Programme(
                        "Licenciatura em Engenharia Informática e de Computadores",
                        "LEIC"
                    ),
                    "2020-2021-2",
                    "LEIC11Da",
                    1,
                    listOf()
                )
            ),
            listOf()
        )
    }
}

@RunWith(MockitoJUnitRunner::class)
@SpringBootTest

class WriteFileTaskletTests {
    @Autowired
    private lateinit var state: ISELTimetableJob.State

    private lateinit var writeFileTaskletSuccess: WriteFileTasklet
    private lateinit var writeFileTaskletFailure: WriteFileTasklet

    private lateinit var stepContribution: StepContribution
    private lateinit var chunkContext: ChunkContext

    private lateinit var timetableDto: TimetableDto

    val mockDispatcherSuccess = mock<IDispatcher> {
        on { dispatch(any(), any(), any()) } doReturn DispatchResult.SUCCESS
    }

    val mockDispatcherFailure = mock<IDispatcher> {
        on { dispatch(any(), any(), any()) } doReturn DispatchResult.FAILURE
    }

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
        writeFileTaskletSuccess = WriteFileTasklet(mockDispatcherSuccess)
        writeFileTaskletFailure = WriteFileTasklet(mockDispatcherFailure)
    }

    @Test
    fun `when write file to disk then if file exists returns success`() {
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

        assertEquals(RepeatStatus.FINISHED, writeFileTaskletSuccess.execute(stepContribution, chunkContext))
    }

    @Test
    fun `when dispatcher returns failure then execute tasklet execution ends`() {
        assertEquals(RepeatStatus.FINISHED, writeFileTaskletFailure.execute(stepContribution, chunkContext))
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
            writeFileTaskletSuccess.generateTimetableDataFromDto(badTimetableDto)
        }
    }
}
