package org.ionproject.integration.dispatcher.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File

private val LOGGER = LoggerFactory.getLogger(GitHandlerImpl::class.java)

@Service
class GitHandlerImpl : IGitHandler {

    private lateinit var repositoryMetadata: GitRepoData
    private lateinit var git: Git

    @Service
    companion object Factory : IGitHandlerFactory {

        override fun checkout(stagingDir: String, repoData: GitRepoData): IGitHandler {
            val repoDirectory = File("$stagingDir${File.separator}${repoData.name}")
            cleanStagingArea(repoDirectory)
            LOGGER.info("Cloning Git repository from ${repoData.url}")

            val repo = Git.cloneRepository()
                .setURI(repoData.url)
                .setDirectory(repoDirectory)
                .setCredentialsProvider(UsernamePasswordCredentialsProvider(repoData.user, repoData.password))
                .call()
            return GitHandlerImpl().apply {
                repositoryMetadata = repoData
                git = repo
            }
        }

        private fun cleanStagingArea(path: File) {
            path.deleteRecursively()
        }
    }

    override fun commit(message: String) {
        git.add()
            .addFilepattern(".")
            .call()

        git.commit()
            .setMessage(message)
            .call()
    }

    override fun push() {
        git.push()
            .setCredentialsProvider(
                UsernamePasswordCredentialsProvider(
                    repositoryMetadata.user,
                    repositoryMetadata.password
                )
            )
            .call()
    }

    override fun update(): GitOutcome {
        val result = git.pull()
            .setCredentialsProvider(
                UsernamePasswordCredentialsProvider(
                    repositoryMetadata.user,
                    repositoryMetadata.password
                )
            )
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
