package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.JobEngine
import org.ionproject.integration.dispatcher.CalendarTerm
import org.ionproject.integration.dispatcher.DispatchResult
import org.ionproject.integration.dispatcher.IDispatcher
import org.ionproject.integration.dispatcher.InstitutionMetadata
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.dispatcher.ProgrammeMetadata
import org.ionproject.integration.dispatcher.Term
import org.ionproject.integration.dispatcher.TimetableData
import org.ionproject.integration.job.ISELTimetableJob
import org.ionproject.integration.model.external.timetable.TimetableDto
import org.ionproject.integration.utils.Institution
import org.slf4j.LoggerFactory
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
    private val state: ISELTimetableJob.State,
    private val dispatcher: IDispatcher<TimetableData>
) : Tasklet {

    private val log = LoggerFactory.getLogger(WriteFileTasklet::class.java)

    @Value("#{jobParameters['${JobEngine.FORMAT_PARAMETER}']}")
    private var format: OutputFormat = OutputFormat.JSON

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        return when (val writeResult = writeToGit()) {
            DispatchResult.SUCCESS -> RepeatStatus.FINISHED
            else -> {
                log.error("Error Writing to Git: $writeResult")
                RepeatStatus.FINISHED
            }
        }
    }

    private fun writeToGit(): DispatchResult {
        val timetable = TimetableDto.from(state.timetableTeachers)
        return dispatcher.dispatch(generateTimetableDataFromDto(timetable), format)
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
