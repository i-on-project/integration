package org.ionproject.integration.infrastructure.exception

sealed class IntegrationException(message: String) : RuntimeException(message)

class ArgumentException(message: String) : IntegrationException(message)
