package org.ionproject.integration.infrastructure

import org.ionproject.integration.infrastructure.exceptions.ArgumentException

enum class OutputFormat(val extension: String) {
    YAML(".yml"),
    JSON(".json");

    companion object {
        fun of(name: String): OutputFormat =
            values().firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }
                ?: throw ArgumentException("Invalid format: $name")
    }
}