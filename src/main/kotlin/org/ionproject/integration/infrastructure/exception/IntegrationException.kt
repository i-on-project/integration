package org.ionproject.integration.infrastructure.exception

import java.net.URI

sealed class IntegrationException(message: String) : RuntimeException(message) {
    abstract val definitionUri: URI
    abstract val title: String
}
