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
    @Max(5)
    @Min(0)
    var coreRetries: Int = 0

    @NotEmpty
    lateinit var resourcesFolder: String
}
