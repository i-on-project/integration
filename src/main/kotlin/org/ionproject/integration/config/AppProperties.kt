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
}
