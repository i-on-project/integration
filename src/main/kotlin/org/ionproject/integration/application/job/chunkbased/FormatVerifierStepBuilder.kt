package org.ionproject.integration.application.job.chunkbased

import org.ionproject.integration.domain.timetable.RawTimetableData
import org.ionproject.integration.utils.Try
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component("FormatVerifierStepBuilder")
class FormatVerifierStepBuilder(private val stepBuilderFactory: StepBuilderFactory) {

    fun build(
        r: ItemReader<RawTimetableData>,
        p: ItemProcessor<RawTimetableData, Try<Boolean>>,
        w: ItemWriter<Try<Boolean>>
    ): TaskletStep {
        return stepBuilderFactory.get("Verify Format")
            .chunk<RawTimetableData, Try<Boolean>>(1)
            .reader(r)
            .processor(p)
            .writer(w)
            .build()
    }
}
