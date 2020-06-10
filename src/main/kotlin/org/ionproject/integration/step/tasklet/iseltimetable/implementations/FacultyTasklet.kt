package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

class FacultyTasklet(val properties: ISELTimetableProperties, val state: ISELTimetable.State) :
    Tasklet {
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        val json = Try.ofValue(state.timetableTeachers.teachers.toTypedArray())
            .flatMap { t -> JsonUtils.toJson(t) }

        println(json.orThrow())
        return RepeatStatus.FINISHED
    }
}
