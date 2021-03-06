package org.ionproject.integration.infrastructure.git

sealed interface IGitHandler {
    fun commit(message: String)

    fun add(input: String = "*")

    fun push(): GitOutcome

    fun update()
}

sealed interface IGitHandlerFactory {
    fun checkout(stagingDir: String, repoData: GitRepoData, timeout: Int): IGitHandler
}

data class GitRepoData(
    val name: String,
    val url: String,
    val user: String,
    val password: String,
    val branch: String
)

enum class GitOutcome {
    SUCCESS {
        override val isSuccessful: Boolean = true
    },
    CONFLICT {
        override val isSuccessful: Boolean = false
    };

    abstract val isSuccessful: Boolean
}
