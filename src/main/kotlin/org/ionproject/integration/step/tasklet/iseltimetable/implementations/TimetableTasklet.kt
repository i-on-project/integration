package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

class TimetableTasklet(val props: ISELTimetableProperties, val state: ISELTimetable.State) :
    Tasklet {
    val log = LoggerFactory.getLogger(TimetableTasklet::class.java)
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        println("sent timetable to core - ${state.timetableTeachers.timetable}")

        val json = Try.ofValue(state.timetableTeachers.timetable.toTypedArray())
            .flatMap { t -> JsonUtils.toJson(t) }

        log.info(json.orThrow())
        return RepeatStatus.FINISHED
    }
}
