package org.ionproject.integration.dispatcher

import org.ionproject.integration.config.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

private const val TIMETABLE_FILENAME = "timetable"
private const val PROGRAMMES = "programmes"

@Component
class TimetableFileWriter(private val serializer: ISerializer) {

    @Autowired
    private lateinit var props: AppProperties

    val staging by lazy { props.stagingDir }
    val repositoryName by lazy { props.gitRepository }

    fun write(timetable: TimetableData, format: OutputFormat): File {

        val dir = getDirectory(timetable)
        with(dir.asFile) {
            if (!exists())
                mkdirs()
        }

        val serializedData = serializer.serialize(timetable.data, format)

        val file = dir + (TIMETABLE_FILENAME + format.extension)
        return file.asFile.apply { writeText(serializedData) }
    }

    private fun getDirectory(timetable: TimetableData): Filepath {
        val pathType = if (staging.first() == '/') Filepath.PathType.ABSOLUTE else Filepath.PathType.RELATIVE

        val rootSegments = if (staging.contains('/')) {
            staging.split('/').filter(String::isNotBlank)
        } else {
            listOf(staging)
        }

        val segments = rootSegments + listOf(
            repositoryName,
            timetable.programme.institution.domain,
            PROGRAMMES,
            timetable.programme.acronym,
            timetable.term.toString()
        )

        return Filepath(segments, pathType, Filepath.CaseType.LOWER)
    }
}
