package org.ionproject.integration.infrastructure.text

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.springframework.stereotype.Component

@Component
class SerializerImpl : ISerializer {
    private val jsonMapper by lazy { jacksonObjectMapper() }
    private val yamlMapper by lazy { ObjectMapper(YAMLFactory()) }

    override fun serialize(source: Any, format: OutputFormat): String =
        when (format) {
            OutputFormat.JSON -> jsonMapper.writeValueAsString(source)
            OutputFormat.YAML -> yamlMapper.writeValueAsString(source)
        }
}
