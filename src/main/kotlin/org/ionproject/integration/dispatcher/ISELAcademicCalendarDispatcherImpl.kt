package org.ionproject.integration.dispatcher

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.dispatcher.git.GitOutcome
import org.ionproject.integration.dispatcher.git.GitRepoData
import org.ionproject.integration.dispatcher.git.IGitHandlerFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class ISELAcademicCalendarDispatcherImpl(
    val fileWriter: FileWriter,
    val gitFactory: IGitHandlerFactory
) : IDispatcher<AcademicCalendarData> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    private val LOGGER = LoggerFactory.getLogger(ISELAcademicCalendarDispatcherImpl::class.java)

    @Autowired
    internal lateinit var props: AppProperties

    val staging by lazy { props.stagingFilesDir }
    val repositoryName by lazy { props.gitRepository }

    private val ACADEMIC_CALENDAR_FILENAME = "calendar"
    private val ACADEMIC_YEARS_FOLDER_NAME = "academic_years"

    private fun getDirectory(academicCalendar: AcademicCalendarData): Filepath {
        val segments = listOf(
            repositoryName,
            academicCalendar.institution.domain,
            ACADEMIC_YEARS_FOLDER_NAME,
            academicCalendar.academicYear
        )

        return staging + segments
    }

    private val git by lazy {
        val data = GitRepoData(
            name = props.gitRepository,
            url = props.gitRepoUrl,
            user = props.gitUser,
            password = props.gitPassword,
            branch = props.gitBranchName
        )
        gitFactory.checkout(props.stagingFilesDir.path, data, props.timeoutInSeconds)
    }

    override fun dispatch(data: AcademicCalendarData, format: OutputFormat): DispatchResult =
        runCatching {
            if (git.update() == GitOutcome.CONFLICT)
                throw IllegalStateException("Unresolved conflict while updating Git Repo")

            fileWriter.write(data, format, ACADEMIC_CALENDAR_FILENAME, getDirectory(data))
            git.add()
            git.commit(generateCommitMessage(data))

            git.push()
        }.onFailure {
            LOGGER.error("Error submitting to git server: ${it.message ?: it}", it)
        }.run {
            if (isSuccess)
                DispatchResult.SUCCESS
            else
                DispatchResult.FAILURE
        }

    private fun generateCommitMessage(data: AcademicCalendarData): String {
        val now = LocalDateTime.now().format(formatter)
        return "${data.javaClass.simpleName}:${data.institution.acronym}:${data.academicYear} at $now"
    }
}
