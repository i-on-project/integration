package org.ionproject.integration.application.dispatcher

import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.application.dto.ParsedData
import org.ionproject.integration.infrastructure.DateUtils.formatToISO8601
import org.ionproject.integration.infrastructure.file.IFileWriter
import org.ionproject.integration.infrastructure.git.GitOutcome
import org.ionproject.integration.infrastructure.git.GitRepoData
import org.ionproject.integration.infrastructure.git.IGitHandlerFactory
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.lang.IllegalStateException
import java.time.ZonedDateTime

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class DispatcherImpl(
    val fileWriter: IFileWriter<ParsedData>,
    val gitFactory: IGitHandlerFactory
) : IDispatcher {
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
            git.update()
            val fileDir = data.getDirectory(repositoryName, staging)
            val file = fileWriter.write(data, format, filename, fileDir)
            git.add()
            git.commit(generateCommitMessage(data))

            val result = git.push()

            if (result == GitOutcome.CONFLICT)
                throw IllegalStateException("Git push failed due to concurrent modification in '${file.path}'")
        }.onFailure {
            LOGGER.error("Error submitting to git server: ${it.message ?: it}", it)
        }.run {
            if (isSuccess)
                DispatchResult.SUCCESS
            else
                DispatchResult.FAILURE
        }

    private fun generateCommitMessage(data: ParsedData): String {
        val now = formatToISO8601(ZonedDateTime.now())
        return "${data.identifier} at $now"
    }
}
