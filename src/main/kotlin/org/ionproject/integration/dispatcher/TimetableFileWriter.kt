package org.ionproject.integration.dispatcher

import org.springframework.stereotype.Component
import java.io.File

const val STAGING_ROOT = "staging"
const val REPOSITORY_ROOT = "integration-data"
private const val TIMETABLE_FILENAME = "timetable"
private const val PROGRAMMES = "programmes"

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

    private fun getDirectory(timetable: TimetableData): File {
        val segments = listOf(
            STAGING_ROOT,
            REPOSITORY_ROOT,
            timetable.programme.institution.domain,
            PROGRAMMES,
            timetable.programme.acronym,
            timetable.term.toString()
        )

        return Filepath(segments, caseType = Filepath.CaseType.LOWER).asFile
    }
}
