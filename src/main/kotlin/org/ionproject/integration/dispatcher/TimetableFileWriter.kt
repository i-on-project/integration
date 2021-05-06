package org.ionproject.integration.dispatcher

import org.springframework.stereotype.Component
import java.io.File

const val STAGING_ROOT = "staging"
private const val TIMETABLE_FILENAME = "timetable"

@Component
class TimetableFileWriter(private val serializer: ISerializer) {
    fun write(data: TimetableData, format: OutputFormat): File {
        val dir = PathBuilder(STAGING_ROOT)
            .setCaseType(PathBuilder.CaseType.LOWER)
            .add(data.programme.institution.domain)
            .add("programmes")
            .add(data.programme.acronym)
            .add(data.term.toString())
            .build()

        if (!dir.exists())
            dir.mkdirs()

        val finalPath = "${dir.path}${File.separator}$TIMETABLE_FILENAME${format.extension}"
        val file = File(finalPath)

        file.writeText(serializer.serialize(data.data, format))

        return file
    }
}
