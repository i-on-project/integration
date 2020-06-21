package org.ionproject.integration.step.utils

import org.mockito.Mockito
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.scope.context.StepContext
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.test.MetaDataInstanceFactory

class SpringBatchTestUtils {

    fun createChunkContext(): ChunkContext {
        val stepExecution =
            Mockito.mock(
                StepExecution::class.java
            )
        val stepContext = Mockito.mock(StepContext::class.java)
        val chunkContext = Mockito.mock(ChunkContext::class.java)
        val jobExecution = createJobExecution()
        Mockito.`when`(chunkContext.stepContext)
            .thenReturn(stepContext)
        Mockito.`when`(stepContext.stepExecution).thenReturn(stepExecution)
        Mockito.`when`(stepExecution.jobExecution)
            .thenReturn(jobExecution)
        return chunkContext
    }

    private fun createJobExecution(): JobExecution {
        return MetaDataInstanceFactory.createJobExecution()
    }

    fun createStepExecution(): StepExecution {
        return MetaDataInstanceFactory.createStepExecution()
    }

    fun createExecutionContext(): ExecutionContext {
        return MetaDataInstanceFactory.createJobExecution().executionContext
    }
}
