package org.ionproject.integration.infrastructure.exception

import org.ionproject.integration.application.config.PROJECT_DOCUMENTATION_ROOT
import java.net.URI

private const val URI = "${PROJECT_DOCUMENTATION_ROOT}TokenMissingException.md"

class TokenMissingException : IntegrationException("A valid authentication token was not provided") {
    override val definitionUri: URI
        get() = URI(URI)
    override val title: String
        get() = "Token missing"
}
