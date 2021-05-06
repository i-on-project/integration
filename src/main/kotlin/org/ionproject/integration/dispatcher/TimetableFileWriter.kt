package org.ionproject.integration.dispatcher

import org.springframework.stereotype.Component
import java.io.File

const val STAGING_ROOT = "staging"
private const val TIMETABLE_FILENAME = "timetable"

@Component
class TimetableFileWriter(private val serializer: ISerializer) {
    fun write(timetable: TimetableData, format: OutputFormat): File {

        val dir = getDirectory(timetable)
        if (!dir.exists())
            dir.mkdirs()

        val serializedData = serializer.serialize(timetable.data, format)

        val finalPath = "${dir.path}${File.separator}$TIMETABLE_FILENAME${format.extension}"
        return File(finalPath).apply { writeText(serializedData) }
    }

    private fun getDirectory(timetable: TimetableData): File =
        PathBuilder(STAGING_ROOT)
            .setCaseType(PathBuilder.CaseType.LOWER)
            .add(timetable.programme.institution.domain)
            .add("programmes")
            .add(timetable.programme.acronym)
            .add(timetable.term.toString())
            .build()
}
