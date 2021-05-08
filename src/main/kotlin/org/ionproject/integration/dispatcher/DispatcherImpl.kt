package org.ionproject.integration.dispatcher

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.ionproject.integration.config.AppProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val LOGGER = LoggerFactory.getLogger(DispatcherImpl::class.java)

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class DispatcherImpl(val timetableFileWriter: TimetableFileWriter) : ITimetableDispatcher {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    @Autowired
    private lateinit var props: AppProperties

    private val repository by lazy {
        val repoDirectory = File("${props.stagingDir}${File.separator}${props.gitRepository}")
        repoDirectory.deleteRecursively()

        LOGGER.info("Cloning Git repository from ${props.gitRepoUrl}")

        Git.cloneRepository()
            .setURI(props.gitRepoUrl)
            .setDirectory(repoDirectory)
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(props.gitUser, props.gitPassword))
            .call()
    }

    override fun dispatch(data: TimetableData, format: OutputFormat): DispatchResult {

        val mergeResult = repository.pull()
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(props.gitUser, props.gitPassword))
            .call()
            .mergeResult

        LOGGER.info("Merge result: ${mergeResult.mergeStatus}")

        timetableFileWriter.write(data, OutputFormat.JSON)
        repository.add().addFilepattern(".").call()

        repository.commit().setMessage(generateCommitMessage(data)).call()
        repository.push().setCredentialsProvider(UsernamePasswordCredentialsProvider(props.gitUser, props.gitPassword))
            .call()
        return DispatchResult.SUCCESS
    }

    private fun generateCommitMessage(timetableData: TimetableData): String {
        val now = LocalDateTime.now().format(formatter)
        return "${timetableData.javaClass.simpleName}:${timetableData.programme.acronym}:${timetableData.term} at $now"
    }
}
