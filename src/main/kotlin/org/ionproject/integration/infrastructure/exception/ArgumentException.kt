package org.ionproject.integration.infrastructure.exception

import org.ionproject.integration.application.config.PROJECT_DOCUMENTATION_ROOT
import java.net.URI

private const val URI = "${PROJECT_DOCUMENTATION_ROOT}ArgumentException.md"

class ArgumentException(message: String) : IntegrationException(message) {
    override val definitionUri: URI = URI(URI)
    override val title: String
        get() = "Invalid or missing argument"
}
