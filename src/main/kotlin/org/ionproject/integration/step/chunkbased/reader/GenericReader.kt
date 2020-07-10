package org.ionproject.integration.step.chunkbased.reader

import java.nio.file.Path
import org.ionproject.integration.builder.implementations.GenericFactory
import org.ionproject.integration.file.implementations.FileDigestImpl
import org.ionproject.integration.model.internal.generic.IInternalModel
import org.ionproject.integration.model.internal.generic.JobType
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@StepScope
@Component("GenericReader")
class GenericReader : ItemReader<IInternalModel> {
    private lateinit var stepExecution: StepExecution

    @BeforeStep
    fun saveStepExecution(stepExecution: StepExecution) {
        this.stepExecution = stepExecution
    }

    @Value("#{jobExecutionContext['file-path']}")
    private lateinit var path: Path

    @Value("#{jobParameters['jobType']}")
    private lateinit var jobType: String

    private var nItems: Int = 0

    override fun read(): IInternalModel? {
        if (nItems > 0)
            return null

        try {
            val typeEnum = enumValueOf<JobType>(jobType)
            val fd = FileDigestImpl()

            val f = GenericFactory.getFactory(typeEnum)

            val imodel = f.parse(path)

            val fileHash = fd.digest(path.toFile())
            stepExecution.jobExecution.executionContext.put("file-hash", fileHash)

            nItems += 1
            return imodel
        } finally {
            path.toFile().delete()
        }
    }
}
