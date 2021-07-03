package org.ionproject.integration.infrastructure.file

import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.text.containsCaseInsensitive

internal const val INVALID_FORMAT_ERROR = "Invalid format: %s"

enum class OutputFormat(val extension: String, private val alternativeNames: List<String> = emptyList()) {
    YAML(".yml", listOf("yml")),
    JSON(".json");

    companion object {
        fun of(name: String): OutputFormat {
            val trimName = name.trim()
            return values().firstOrNull { format ->
                format.nameEquals(trimName) || format.alternativeNames.containsCaseInsensitive(trimName)
            }
                ?: throw ArgumentException(INVALID_FORMAT_ERROR.format(trimName))
        }
    }

    private fun nameEquals(name: String): Boolean = this.name.equals(name, ignoreCase = true)
}
