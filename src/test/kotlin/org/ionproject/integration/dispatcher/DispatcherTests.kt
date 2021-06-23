package org.ionproject.integration.dispatcher

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.DispatcherImpl
import org.ionproject.integration.infrastructure.GitOutcome
import org.ionproject.integration.infrastructure.IGitHandler
import org.ionproject.integration.infrastructure.IGitHandlerFactory
import org.ionproject.integration.infrastructure.OutputFormat
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

    private val mockWriter = mock<IFileWriter<ParsedData>> {
        on { write(any(), any(), any(), any()) } doReturn File("")
    }

    private val appProps = mock<AppProperties> {
        on { gitBranchName } doReturn "mock"
        on { stagingDir } doReturn "mock"
        on { tempDir } doReturn "mock"
        on { gitRepository } doReturn "mock"
        on { gitRepoUrl } doReturn "mock"
        on { gitUser } doReturn "mock"
        on { gitPassword } doReturn "mock"
        on { stagingFilesDir } doReturn Filepath(listOf("mock"))
        on { tempFilesDir } doReturn Filepath(listOf("mock"))
    }

    @Test
    fun `when given a working git handler then success`() {
        val dispatcher = DispatcherImpl(mockWriter, getMockFactory(true)).apply { props = appProps }
        val outcome = dispatcher.dispatch(meta, "test", OutputFormat.YAML)

        assertEquals(DispatchResult.SUCCESS, outcome)
    }

    @Test
    fun `when given a broken git handler then failure`() {
        val dispatcher = DispatcherImpl(mockWriter, getMockFactory(false)).apply { props = appProps }
        val outcome = dispatcher.dispatch(meta, "test", OutputFormat.YAML)

        assertEquals(DispatchResult.FAILURE, outcome)
    }
}
