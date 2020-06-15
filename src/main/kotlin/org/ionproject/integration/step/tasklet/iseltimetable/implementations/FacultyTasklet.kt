package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@Component
class FacultyTasklet(private val state: ISELTimetable.State) :
    Tasklet {
    private val log = LoggerFactory.getLogger(FacultyTasklet::class.java)
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        val json = Try.ofValue(state.timetableTeachers.teachers.toTypedArray())
            .flatMap { t -> JsonUtils.toJson(t) }
            .orThrow()

        log.info(json)
        return RepeatStatus.FINISHED
    }
}
