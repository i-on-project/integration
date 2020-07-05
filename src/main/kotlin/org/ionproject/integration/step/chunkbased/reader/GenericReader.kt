package org.ionproject.integration.step.chunkbased.reader

import java.nio.file.Path
import org.ionproject.integration.builder.implementations.GenericFactory
import org.ionproject.integration.model.internal.generic.IInternalModel
import org.ionproject.integration.model.internal.generic.JobType
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@StepScope
@Component("GenericReader")
class GenericReader : ItemReader<IInternalModel> {
    @Value("#{jobExecutionContext['file-path']}")
    private lateinit var path: Path

    @Value("#{jobParameters['jobType']}")
    private lateinit var jobType: String

    override fun read(): IInternalModel? {
        try {
            val typeEnum = enumValueOf<JobType>(jobType)

            val f = GenericFactory.getFactory(typeEnum)

            // factory.parse(path)
            val imodel = f.parse(path)

            return imodel
        } finally {
            path.toFile().delete()
        }
    }
}
