package org.ionproject.integration.dispatcher

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.dispatcher.git.GitRepoData
import org.ionproject.integration.dispatcher.git.IGitHandlerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class DispatcherImpl(
    val timetableFileWriter: TimetableFileWriter,
    gitFactory: IGitHandlerFactory
) : ITimetableDispatcher {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    @Autowired
    private lateinit var props: AppProperties

    private val git by lazy {
        val data = GitRepoData(
            name = props.gitRepository,
            url = props.gitRepoUrl,
            user = props.gitUser,
            password = props.gitPassword,
            branch = props.gitBranchName
        )
        gitFactory.checkout(props.stagingDir, data)
    }

    override fun dispatch(data: TimetableData, format: OutputFormat): DispatchResult =
        runCatching {
            git.update()

            timetableFileWriter.write(data, format)
            git.add()
            git.commit(generateCommitMessage(data))

            git.push()
        }.run {
            if (isSuccess)
                DispatchResult.SUCCESS
            else
                DispatchResult.FAILURE
        }

    private fun generateCommitMessage(timetableData: TimetableData): String {
        val now = LocalDateTime.now().format(formatter)
        return "${timetableData.javaClass.simpleName}:${timetableData.programme.acronym}:${timetableData.term} at $now"
    }
}
