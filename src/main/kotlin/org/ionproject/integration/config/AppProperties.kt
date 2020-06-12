package org.ionproject.integration.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ion")
class AppProperties {
    var coreBaseUrl: String = ""
    var coreToken: String = ""
    var coreRequestTimeoutSeconds: Int = 0
    var coreRetries: Int = 0
}