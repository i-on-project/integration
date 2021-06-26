package org.ionproject.integration.infrastructure.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.RemoteRefUpdate
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.ionproject.integration.infrastructure.git.GitHandlerImpl.Factory.getAllBranchNames
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.io.File
import java.lang.IllegalStateException

private val LOGGER = LoggerFactory.getLogger(GitHandlerImpl::class.java)
private const val REMOTE_REF_STRING = "refs/remotes/origin/"
private const val HEAD_REF_STRING = "refs/heads/"

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

            val credentialProvider = UsernamePasswordCredentialsProvider(repoData.user, repoData.password)

            LOGGER.info("Cloning Git repository from ${repoData.url}.")
            val repo = Git.cloneRepository()
                .setURI(repoData.url)
                .setDirectory(repoDirectory)
                .setCredentialsProvider(credentialProvider)
                .setCloneAllBranches(true)
                .setTimeout(timeout)
                .call()

            val branchName = repoData.branch
            if (!isBranchCreated(repo, branchName)) {
                LOGGER.info("Branch '$branchName' does not exist. New local branch will be created and published.")
                createRemoteBranch(repo, branchName, credentialProvider)
            }

            checkoutBranch(repo, branchName)

            return GitHandlerImpl().apply {
                repositoryMetadata = repoData
                git = repo
                credentials = credentialProvider
                timeoutInSeconds = timeout
            }
        }

        private fun isBranchCreated(git: Git, branchName: String): Boolean {
            val branches = git.getAllBranchNames()
            return branches.contains(branchName)
        }

        private fun createRemoteBranch(repo: Git, branchName: String, credentialsProvider: CredentialsProvider) {
            val branch = repo.createLocalBranch(branchName)
                ?: throw IllegalStateException("Could not create local branch.")
            repo.publishBranchToRemote(credentialsProvider, branch)
        }

        private fun checkoutBranch(repo: Git, branchName: String) {
            LOGGER.info("Switching to branch '$branchName'.")
            repo.checkout()
                .setName(branchName)
                .call()
        }

        private fun cleanStagingArea(path: File) {
            path.deleteRecursively()
        }

        private fun Git.getAllBranchNames(): List<String> {
            fetch()
                .setRemoveDeletedRefs(true)
                .call()

            val branchRefs = branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()

            return branchRefs
                .filter { ref -> ref.name.startsWith(REMOTE_REF_STRING) }
                .map { ref ->
                    ref.name.substringAfter(REMOTE_REF_STRING)
                }
        }

        private fun Git.createLocalBranch(branchName: String): Ref? =
            branchCreate()
                .setName(branchName)
                .setForce(true)
                .call()

        private fun Git.publishBranchToRemote(credentialsProvider: CredentialsProvider, branch: Ref) {
            push()
                .setCredentialsProvider(credentialsProvider)
                .setRefSpecs(RefSpec(branch.name))
                .call()
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

    override fun push(): GitOutcome {
        val res = git.push()
            .setCredentialsProvider(credentials)
            .setTimeout(timeoutInSeconds)
            .call()

        val errorStatus = res.map { ref -> ref.getRemoteUpdate("refs/heads/${repositoryMetadata.branch}").status }
            .firstOrNull { update -> update != RemoteRefUpdate.Status.OK }

        return if (errorStatus == null)
            GitOutcome.SUCCESS
        else {
            LOGGER.info("Git push failed: ${errorStatus.name}")
            GitOutcome.CONFLICT
        }
    }

    override fun update() {
        if (!isBranchCreated(git, repositoryMetadata.branch)) {
            createRemoteBranch(git, repositoryMetadata.branch, credentials)
            checkoutBranch(git, repositoryMetadata.branch)
        }

        git.pull()
            .setCredentialsProvider(credentials)
            .setTimeout(timeoutInSeconds)
            .call()
            .mergeResult
            .mergeStatus
    }
}
