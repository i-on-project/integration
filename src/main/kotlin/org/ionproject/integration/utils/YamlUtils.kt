package org.ionproject.integration.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

object YamlUtils {
    private val mapper = ObjectMapper(YAMLFactory())
        .registerModule(KotlinModule())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun <T> fromYaml(file: File, klass: Class<T>): Try<T> {
        if (!file.exists() || file.isDirectory) {
            return Try.ofError<YAMLException>(YAMLException("File $file does not exist or is a directory."))
        }
        return Try.of { mapper.readValue(file, klass) }
            .mapError { throw YAMLException("Invalid yaml") }
    }

    fun <T> fromYaml(yamlBytes: ByteArray, klass: Class<T>): Try<T> {
        if (yamlBytes.isEmpty()) {
            return Try.ofError<YAMLException>(YAMLException("Empty yaml byte array."))
        }
        return Try.of { mapper.readValue(yamlBytes, klass) }
            .mapError { throw YAMLException("Invalid yaml") }
    }

    fun <T : Any> toYaml(t: T): Try<String> {
        return Try.of { mapper.writeValueAsString(t) }
            .mapError { YAMLException("Could not stringify value $t") }
    }
}
