package org.ionproject.integration.step.chunkbased.processor

import org.ionproject.integration.model.internal.generic.ICoreModel
import org.ionproject.integration.model.internal.generic.IInternalModel
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@StepScope
@Component("GenericProcessor")
class GenericProcessor : ItemProcessor<IInternalModel, ICoreModel> {
    override fun process(item: IInternalModel): ICoreModel? {
        return item.toCore()
    }
}
