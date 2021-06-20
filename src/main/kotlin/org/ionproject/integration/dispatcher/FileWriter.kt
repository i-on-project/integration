package org.ionproject.integration.dispatcher

import org.springframework.stereotype.Component
import java.io.File

@Component
class FileWriter(private val serializer: ISerializer) {

    fun write(timetable: ParsedData, format: OutputFormat, filename: String, filepath: Filepath): File {

        with(filepath.asFile) {
            if (!exists())
                mkdirs()
        }

        val serializedData = serializer.serialize(timetable.data, format)

        val file = filepath + (filename + format.extension)
        return file.asFile.apply { writeText(serializedData) }
    }
}
