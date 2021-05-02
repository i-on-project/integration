package org.ionproject.integration.filerepository.serializer

import org.ionproject.integration.model.OutputFormat

interface ISerializer {
    fun serialize(source: Any, format: OutputFormat): String
}
