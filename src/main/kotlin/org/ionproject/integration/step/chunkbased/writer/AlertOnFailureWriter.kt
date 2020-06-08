package org.ionproject.integration.step.chunkbased.writer

import org.ionproject.integration.alert.interfaces.AlertChannel
import org.ionproject.integration.config.ISELTimetableProperties
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class AlertOnFailureWriter(private val props: ISELTimetableProperties, private val alert: AlertChannel) : ItemWriter<Try<Boolean>> {

    override fun write(items: MutableList<out Try<Boolean>>) {
        val item = items.first()
        item.match({ alert.sendSuccessAlert() }, { e -> alert.sendFailureAlert(e) })
        item.orThrow()
    }
}
