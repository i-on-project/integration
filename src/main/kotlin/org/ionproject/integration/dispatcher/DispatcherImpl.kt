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

private val LOGGER = LoggerFactory.getLogger(DispatcherImpl::class.java)

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class DispatcherImpl(val timetableFileWriter: TimetableFileWriter) : ITimetableDispatcher {

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

        repository.add()
        return DispatchResult.SUCCESS
    }
}
