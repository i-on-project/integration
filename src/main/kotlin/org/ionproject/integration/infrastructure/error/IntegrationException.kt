package org.ionproject.integration.infrastructure.error

sealed class IntegrationException(message: String) : RuntimeException(message)

class ArgumentException(message: String) : IntegrationException(message)
