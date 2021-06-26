package org.ionproject.integration.application.job.tasklet

import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.application.job.ISELTimetableJob
import org.ionproject.integration.application.job.TIMETABLE_JOB_NAME
import org.ionproject.integration.application.dto.CalendarTerm
import org.ionproject.integration.application.dispatcher.DispatchResult
import org.ionproject.integration.application.dispatcher.IDispatcher
import org.ionproject.integration.application.dto.InstitutionMetadata
import org.ionproject.integration.infrastructure.file.OutputFormat
import org.ionproject.integration.application.dto.ProgrammeMetadata
import org.ionproject.integration.domain.common.Term
import org.ionproject.integration.application.dto.TimetableData
import org.ionproject.integration.domain.timetable.dto.TimetableDto
import org.ionproject.integration.infrastructure.text.Institution
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@StepScope
class WriteFileTasklet(
    private val dispatcher: IDispatcher
) : Tasklet {

    private val log = LoggerFactory.getLogger(WriteFileTasklet::class.java)

    @Value("#{jobParameters['${JobEngine.FORMAT_PARAMETER}']}")
    private var format: OutputFormat = OutputFormat.JSON

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        return when (val writeResult = writeToGit()) {
            DispatchResult.SUCCESS -> RepeatStatus.FINISHED
            else -> {
                log.error("Error Writing to Git: $writeResult")
                contribution.exitStatus = ExitStatus.FAILED
                RepeatStatus.FINISHED
            }
        }
    }

    private fun writeToGit(): DispatchResult {
        val timetable = TimetableDto.from(ISELTimetableJob.State.timetableTeachers)
        return dispatcher.dispatch(generateTimetableDataFromDto(timetable), TIMETABLE_JOB_NAME, format)
    }

    internal fun generateTimetableDataFromDto(timetableDto: TimetableDto): TimetableData {
        return TimetableData(
            ProgrammeMetadata(
                InstitutionMetadata(
                    timetableDto.school.name,
                    timetableDto.school.acr,
                    Institution.valueOf(timetableDto.school.acr).identifier
                ),
                timetableDto.programme.name,
                timetableDto.programme.acr
            ),
            CalendarTerm(
                timetableDto.calendarTerm.take(4).toInt(),
                when (timetableDto.calendarTerm.takeLast(1).toInt()) {
                    1 -> Term.FALL
                    2 -> Term.SPRING
                    else -> throw IllegalArgumentException("Invalid Term ${timetableDto.calendarTerm}")
                }
            ),
            timetableDto
        )
    }
}
