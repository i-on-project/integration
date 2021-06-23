package org.ionproject.integration.infrastructure.text

import org.ionproject.integration.infrastructure.file.OutputFormat

interface ISerializer {
    fun serialize(source: Any, format: OutputFormat): String
}
