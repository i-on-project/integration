package org.ionproject.integration.dispatcher

import org.ionproject.integration.config.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

private const val ACADEMIC_CALENDAR_FILENAME = "calendar"
private const val ACADEMIC_YEARS_FOLDER_NAME = "academic_years"

@Component
class CalendarFileWriter(private val serializer: ISerializer) {

    @Autowired
    private lateinit var props: AppProperties

    val staging by lazy { props.stagingFilesDir }
    val repositoryName by lazy { props.gitRepository }

    fun write(academicCalendar: AcademicCalendarData, format: OutputFormat): File {

        val dir = getDirectory(academicCalendar)
        with(dir.asFile) {
            if (!exists())
                mkdirs()
        }

        val serializedData = serializer.serialize(academicCalendar.data, format)

        val file = dir + (ACADEMIC_CALENDAR_FILENAME + format.extension)
        return file.asFile.apply { writeText(serializedData) }
    }

    private fun getDirectory(academicCalendar: AcademicCalendarData): Filepath {
        val segments = listOf(
            repositoryName,
            academicCalendar.institution.domain,
            ACADEMIC_YEARS_FOLDER_NAME,
            academicCalendar.academicYear
        )

        return staging + segments
    }
}
