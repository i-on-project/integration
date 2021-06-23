package org.ionproject.integration.infrastructure.exceptions

sealed class IntegrationException(message: String) : RuntimeException(message)

class ArgumentException(message: String) : IntegrationException(message)
