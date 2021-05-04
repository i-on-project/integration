package org.ionproject.integration.dispatcher

interface ISerializer {
    fun serialize(source: Any, format: OutputFormat): String
}
