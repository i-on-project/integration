package org.ionproject.integration.infrastructure

interface ISerializer {
    fun serialize(source: Any, format: OutputFormat): String
}
