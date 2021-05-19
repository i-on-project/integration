package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.dispatcher.CalendarTerm
import org.ionproject.integration.dispatcher.DispatchResult
import org.ionproject.integration.dispatcher.ITimetableDispatcher
import org.ionproject.integration.dispatcher.InstitutionMetadata
import org.ionproject.integration.dispatcher.OutputFormat
import org.ionproject.integration.dispatcher.ProgrammeMetadata
import org.ionproject.integration.dispatcher.Term
import org.ionproject.integration.dispatcher.TimetableData
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.external.timetable.TimetableDto
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(value = "prototype")
class WriteFileTasklet(
    private val state: ISELTimetable.State,
    private val dispatcher: ITimetableDispatcher
) : Tasklet {

    private val log = LoggerFactory.getLogger(WriteFileTasklet::class.java)

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
        return dispatcher.dispatch(generateTimetableDataFromDto(timetable), OutputFormat.JSON)
    }

    internal fun generateTimetableDataFromDto(timetableDto: TimetableDto): TimetableData {
        return TimetableData(
            ProgrammeMetadata(
                InstitutionMetadata(
                    timetableDto.school.name,
                    timetableDto.school.acr,
                    "isel.ipl.pt" // TODO to change in a future release
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
