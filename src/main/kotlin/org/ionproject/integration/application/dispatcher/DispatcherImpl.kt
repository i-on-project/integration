package org.ionproject.integration.application.dispatcher

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dto.ParsedData
import org.ionproject.integration.dispatcher.IFileWriter
import org.ionproject.integration.infrastructure.GitOutcome
import org.ionproject.integration.infrastructure.GitRepoData
import org.ionproject.integration.infrastructure.IGitHandlerFactory
import org.ionproject.integration.infrastructure.OutputFormat
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class DispatcherImpl(
    val fileWriter: IFileWriter<ParsedData>,
    val gitFactory: IGitHandlerFactory
) : IDispatcher {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    private val LOGGER = LoggerFactory.getLogger(DispatcherImpl::class.java)

    @Autowired
    internal lateinit var props: AppProperties

    val staging by lazy { props.stagingFilesDir }
    val repositoryName by lazy { props.gitRepository }

    private val git by lazy {
        val data = GitRepoData(
            name = props.gitRepository,
            url = props.gitRepoUrl,
            user = props.gitUser,
            password = props.gitPassword,
            branch = props.gitBranchName
        )
        gitFactory.checkout(props.stagingFilesDir.path, data, props.timeoutInSeconds)
    }

    override fun dispatch(data: ParsedData, filename: String, format: OutputFormat): DispatchResult =
        runCatching {
            if (git.update() == GitOutcome.CONFLICT)
                throw IllegalStateException("Unresolved conflict while updating Git Repo")

            fileWriter.write(data, format, filename, data.getDirectory(repositoryName, staging))
            git.add()
            git.commit(generateCommitMessage(data))

            git.push()
        }.onFailure {
            LOGGER.error("Error submitting to git server: ${it.message ?: it}", it)
        }.run {
            if (isSuccess)
                DispatchResult.SUCCESS
            else
                DispatchResult.FAILURE
        }

    private fun generateCommitMessage(data: ParsedData): String {
        val now = LocalDateTime.now().format(formatter)
        return "${data.identifier} at $now"
    }
}
