package org.ionproject.integration.dispatcher

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.dispatcher.git.GitOutcome
import org.ionproject.integration.dispatcher.git.IGitHandler
import org.ionproject.integration.dispatcher.git.IGitHandlerFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

class DispatcherTests {
    private val mockHandlerSuccess = mock<IGitHandler> {
        on { update() } doReturn GitOutcome.SUCCESS
    }

    private val mockHandlerFailure = mock<IGitHandler> {
        on { update() } doReturn GitOutcome.CONFLICT
    }

    private fun getMockFactory(isWorking: Boolean): IGitHandlerFactory = mock {
        on { checkout(any(), any(), any()) } doReturn if (isWorking) mockHandlerSuccess else mockHandlerFailure
    }

    private val mockWriter = mock<TimetableFileWriter> {
        on { write(any(), any()) } doReturn File("")
    }

    private val appProps = mock<AppProperties> {
        on { gitBranchName } doReturn ""
        on { stagingDir } doReturn ""
        on { tempDir } doReturn ""
        on { configDir } doReturn ""
        on { gitRepository } doReturn ""
        on { gitRepoUrl } doReturn ""
        on { gitUser } doReturn ""
        on { gitPassword } doReturn ""
        on { stagingFilesDir } doReturn Filepath(listOf("mock"))
        on { configFilesDirTimetableIsel } doReturn Filepath(listOf("mock"))
        on { tempFilesDir } doReturn Filepath(listOf("mock"))
    }

    @Test
    fun `when given a working git handler then success`() {
        val dispatcher = DispatcherImpl(mockWriter, getMockFactory(true)).apply { props = appProps }
        val outcome = dispatcher.dispatch(meta, OutputFormat.YAML)

        assertEquals(DispatchResult.SUCCESS, outcome)
    }

    @Test
    fun `when given a broken git handler then failure`() {
        val dispatcher = DispatcherImpl(mockWriter, getMockFactory(false)).apply { props = appProps }
        val outcome = dispatcher.dispatch(meta, OutputFormat.YAML)

        assertEquals(DispatchResult.FAILURE, outcome)
    }
}
