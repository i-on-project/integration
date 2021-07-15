package org.ionproject.integration.application.config

import org.ionproject.integration.infrastructure.file.Filepath
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

const val PROJECT_DOCUMENTATION_ROOT = "https://github.com/i-on-project/integration/blob/master/docs/infrastructure/"

@Component
@ConfigurationProperties(prefix = "ion")
@Validated
class AppProperties {

    @NotEmpty
    lateinit var configFile: String

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

    @NotEmpty
    lateinit var token: String

    val gitRepoUrl by lazy { "$gitServer$gitRepository.git" }

    val stagingFilesDir by lazy { getAsFilePath(stagingDir) }

    val tempFilesDir by lazy { getAsFilePath(tempDir) }

    val configurationFile by lazy { getAsFilePath(configFile) }

    val timeoutInSeconds = 60

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
