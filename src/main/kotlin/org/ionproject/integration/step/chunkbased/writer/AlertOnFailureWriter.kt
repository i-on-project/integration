package org.ionproject.integration.step.chunkbased.writer

import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class AlertOnFailureWriter() : ItemWriter<Try<Boolean>> {

    private val log = LoggerFactory.getLogger(AlertOnFailureWriter::class.java)

    override fun write(items: MutableList<out Try<Boolean>>) {
        val item = items.first()
        item.match(
            { r -> log.info("Result for Step 2 is $r") },
            { e -> log.info("Step 2 is about to throw exception $e with message ${e.message}") }
        )
        item.orThrow()
    }
}
