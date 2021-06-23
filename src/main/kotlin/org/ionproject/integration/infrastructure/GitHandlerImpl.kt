package org.ionproject.integration.infrastructure

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.io.File

private val LOGGER = LoggerFactory.getLogger(GitHandlerImpl::class.java)

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class GitHandlerImpl : IGitHandler {
    private lateinit var repositoryMetadata: GitRepoData
    private lateinit var git: Git
    private lateinit var credentials: CredentialsProvider
    private var timeoutInSeconds: Int = 30

    @Service
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    companion object Factory : IGitHandlerFactory {

        override fun checkout(stagingDir: String, repoData: GitRepoData, timeout: Int): IGitHandler {
            val repoDirectory = File("$stagingDir${File.separator}${repoData.name}")
            cleanStagingArea(repoDirectory)
            LOGGER.info("Cloning Git repository from ${repoData.url}. Tracking remote branch: ${repoData.branch}.")

            val credentialProvider = UsernamePasswordCredentialsProvider(repoData.user, repoData.password)

            val repo = Git.cloneRepository()
                .setURI(repoData.url)
                .setDirectory(repoDirectory)
                .setCredentialsProvider(credentialProvider)
                .setBranchesToClone(listOf("refs/heads/${repoData.branch}"))
                .setBranch(repoData.branch)
                .setTimeout(timeout)
                .call()
            return GitHandlerImpl().apply {
                repositoryMetadata = repoData
                git = repo
                credentials = credentialProvider
                timeoutInSeconds = timeout
            }
        }

        private fun cleanStagingArea(path: File) {
            path.deleteRecursively()
        }
    }

    override fun commit(message: String) {
        git.commit()
            .setMessage(message)
            .call()
    }

    override fun add(input: String) {
        val toAdd = if (input == "*") "." else input
        git.add()
            .addFilepattern(toAdd)
            .call()
    }

    override fun push() {
        git.push()
            .setCredentialsProvider(credentials)
            .setTimeout(timeoutInSeconds)
            .call()
    }

    override fun update(): GitOutcome {
        val result = git.pull()
            .setCredentialsProvider(credentials)
            .setRemoteBranchName(repositoryMetadata.branch)
            .setTimeout(timeoutInSeconds)
            .call()
            .mergeResult
            .mergeStatus

        LOGGER.info("Merge result: $result")

        return if (result.isSuccessful)
            GitOutcome.SUCCESS
        else
            GitOutcome.CONFLICT
    }
}
