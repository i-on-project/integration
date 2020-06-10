package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.config.ISELTimetableProperties
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

class PostUploadTasklet(val props: ISELTimetableProperties) : Tasklet {
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        val fileHash = chunkContext.stepContext.stepExecution.jobExecution.executionContext.getInt(props.hashKey)
        println(fileHash)
        return RepeatStatus.FINISHED
    }
}
