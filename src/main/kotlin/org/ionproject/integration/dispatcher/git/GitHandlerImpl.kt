package org.ionproject.integration.dispatcher.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File

private val LOGGER = LoggerFactory.getLogger(GitHandlerImpl::class.java)

@Service
class GitHandlerImpl : IGitHandler {
    private lateinit var repositoryMetadata: GitRepoData
    private lateinit var git: Git
    private lateinit var credentials: CredentialsProvider

    @Service
    companion object Factory : IGitHandlerFactory {

        override fun checkout(stagingDir: String, repoData: GitRepoData): IGitHandler {
            val repoDirectory = File("$stagingDir${File.separator}${repoData.name}")
            cleanStagingArea(repoDirectory)
            LOGGER.info("Cloning Git repository from ${repoData.url}")

            val credentialProvider = UsernamePasswordCredentialsProvider(repoData.user, repoData.password)

            val repo = Git.cloneRepository()
                .setURI(repoData.url)
                .setDirectory(repoDirectory)
                .setCredentialsProvider(credentialProvider)
                .call()
            return GitHandlerImpl().apply {
                repositoryMetadata = repoData
                git = repo
                credentials = credentialProvider
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
            .call()
    }

    override fun update(): GitOutcome {
        val result = git.pull()
            .setCredentialsProvider(credentials)
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
