package org.ionproject.integration.application.job.tasklet

import org.ionproject.integration.application.JobEngine.Companion.JOB_HASH_PARAMETER
import javax.sql.DataSource
import org.ionproject.integration.infrastructure.repository.hash.HashRepositoryImpl
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

const val POST_UPLOAD_TASKLET_NAME = "PostUploadTasklet"

@Component(POST_UPLOAD_TASKLET_NAME)
@StepScope
class PostUploadTasklet() : Tasklet {
    private val log = LoggerFactory.getLogger(PostUploadTasklet::class.java)

    @Autowired
    private lateinit var ds: DataSource

    @Value("#{jobParameters['$JOB_HASH_PARAMETER']}")
    private lateinit var jobId: String

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val hr = HashRepositoryImpl(ds)

        val fileHash = getHash(chunkContext)
        hr.putHash(jobId, fileHash)

        return RepeatStatus.FINISHED
    }

    private fun getHash(chunkContext: ChunkContext): ByteArray =
        chunkContext.stepContext.stepExecution.jobExecution.executionContext.get("file-hash") as ByteArray
}
