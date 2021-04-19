package org.ionproject.integration.config

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@ConfigurationProperties(prefix = "ion")
@Validated
class AppProperties {
    @NotEmpty
    lateinit var coreBaseUrl: String

    @NotEmpty
    lateinit var coreToken: String

    @Max(5)
    @Min(1)
    var coreRequestTimeoutSeconds: Int = 0

    @Max(5)
    @Min(0)
    var coreRetries: Int = 0

    @NotEmpty
    lateinit var resourcesFolder: String

    @NotEmpty
    lateinit var localFileOutputFolder: String
}
