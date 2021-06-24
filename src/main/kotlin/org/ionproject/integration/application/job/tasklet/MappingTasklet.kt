package org.ionproject.integration.application.job.tasklet

import org.ionproject.integration.application.job.ISELTimetableJob
import org.ionproject.integration.domain.timetable.IselTimetableTeachersBuilder
import org.ionproject.integration.infrastructure.orThrow
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@Component("MappingTasklet")
class MappingTasklet : Tasklet {
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val builder = IselTimetableTeachersBuilder()

        builder.setTimetable(ISELTimetableJob.State.rawTimetableData)
        builder.setTeachers(ISELTimetableJob.State.rawTimetableData)

        ISELTimetableJob.State.timetableTeachers = builder.getTimetableTeachers().orThrow()

        return RepeatStatus.FINISHED
    }
}
