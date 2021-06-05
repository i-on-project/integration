package org.ionproject.integration.config

import org.ionproject.integration.dispatcher.Filepath
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

@Component
@ConfigurationProperties(prefix = "ion")
@Validated
class AppProperties {

    @NotEmpty
    lateinit var configDir: String

    @NotEmpty
    lateinit var stagingDir: String

    @NotEmpty
    lateinit var tempDir: String

    @NotEmpty
    lateinit var gitServer: String

    @NotEmpty
    lateinit var gitRepository: String

    @NotEmpty
    lateinit var gitUser: String

    lateinit var gitPassword: String

    @NotEmpty
    lateinit var gitBranchName: String

    val gitRepoUrl by lazy { "$gitServer$gitRepository.git" }

    val configFilesDir by lazy { getAsFilePath(configDir) }

    val stagingFilesDir by lazy { getAsFilePath(stagingDir) }

    val tempFilesDir by lazy { getAsFilePath(tempDir) }

    private fun getAsFilePath(path: String): Filepath {
        val pathType = if (path.startsWith("/"))
            Filepath.PathType.ABSOLUTE
        else
            Filepath.PathType.RELATIVE

        val segments = if (path.contains('/')) {
            path.split('/').filter(String::isNotBlank)
        } else {
            listOf(path)
        }

        return Filepath(segments, pathType, Filepath.CaseType.LOWER)
    }
}
