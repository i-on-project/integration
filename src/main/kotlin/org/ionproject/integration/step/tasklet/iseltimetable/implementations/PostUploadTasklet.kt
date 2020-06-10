package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.config.ISELTimetableProperties
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

class PostUploadTasklet(val props: ISELTimetableProperties) : Tasklet {
    private val log = LoggerFactory.getLogger(PostUploadTasklet::class.java)
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        val fileHash = chunkContext.stepContext.stepExecution.jobExecution.executionContext.getInt(props.hashKey)
        log.info("$fileHash")
        return RepeatStatus.FINISHED
    }
}
