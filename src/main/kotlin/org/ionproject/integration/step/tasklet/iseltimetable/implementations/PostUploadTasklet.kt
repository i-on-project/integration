package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component("PostUploadTasklet")
@StepScope
class PostUploadTasklet() : Tasklet {
    private val log = LoggerFactory.getLogger(PostUploadTasklet::class.java)

    @Value("#{jobParameters['hashKey']}")
    private lateinit var hashKey: String

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        val fileHash = chunkContext.stepContext.stepExecution.jobExecution.executionContext.getInt(hashKey)
        log.info("$fileHash")
        return RepeatStatus.FINISHED
    }
}
