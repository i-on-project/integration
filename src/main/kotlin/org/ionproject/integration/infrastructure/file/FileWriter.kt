package org.ionproject.integration.infrastructure.file

import org.ionproject.integration.application.dto.ParsedData
import org.ionproject.integration.infrastructure.text.ISerializer
import org.springframework.stereotype.Component
import java.io.File

interface IFileWriter<in T> {
    fun write(data: T, format: OutputFormat, filename: String, filepath: Filepath): File
}

@Component
class FileWriter(private val serializer: ISerializer) : IFileWriter<ParsedData> {

    override fun write(data: ParsedData, format: OutputFormat, filename: String, filepath: Filepath): File {

        with(filepath.asFile) {
            if (!exists())
                mkdirs()
        }

        val serializedData = serializer.serialize(data.data, format)

        val file = filepath + (filename + format.extension)
        return file.asFile.apply { writeText(serializedData) }
    }
}
