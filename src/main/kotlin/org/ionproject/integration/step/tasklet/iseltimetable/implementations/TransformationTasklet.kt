package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.builder.implementations.IselTimetableTeachersBuilder
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

class TransformationTasklet(var state: ISELTimetable.State) : Tasklet {
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val builder = IselTimetableTeachersBuilder()

        builder.reset()
        builder.setTimetable(state.rawData)
        builder.setTeachers(state.rawData)

        state.timetableTeachers = builder.getTimetableTeachers().orThrow()

        return RepeatStatus.FINISHED
    }
}
