package org.ionproject.integration.step.chunkbased.writer

import org.ionproject.integration.model.external.generic.ICoreModel
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@StepScope
@Component("GenericCoreWriter")
class GenericCoreWriter :
    ItemWriter<ICoreModel> {
    override fun write(items: MutableList<out ICoreModel>) {
        items.forEach { println(JsonUtils.toJson(it).orThrow()) }
    }
}
