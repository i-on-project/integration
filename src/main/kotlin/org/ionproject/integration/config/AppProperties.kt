package org.ionproject.integration.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

@Component
@ConfigurationProperties(prefix = "ion")
@Validated
class AppProperties {
    @NotEmpty
    lateinit var resourcesFolder: String

    @NotEmpty
    lateinit var localFileOutputFolder: String

    @NotEmpty
    lateinit var stagingDir: String

    @NotEmpty
    lateinit var gitServer: String

    @NotEmpty
    lateinit var gitRepository: String

    @NotEmpty
    lateinit var gitUser: String

    @NotEmpty
    lateinit var gitPassword: String

    val gitRepoUrl by lazy { "$gitServer$gitRepository.git" }
}
