package org.ionproject.integration.step.chunkbased.writer

import javax.mail.internet.InternetAddress
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.item.ItemWriter

class AlertOnFailureWriter(private val alertRecipient: InternetAddress) : ItemWriter<Try<Boolean>> {

    private lateinit var stepExecution: StepExecution

    @BeforeStep
    fun saveStepExecution(stepExecution: StepExecution) {
        this.stepExecution = stepExecution
    }

    override fun write(items: MutableList<out Try<Boolean>>) {
        println("Step 2 - End")
        val item = items.first()

        item.match({}, { e -> alert(e) })

        item.orThrow()
    }
    private fun alert(e: Exception) {
        println("${alertRecipient.toUnicodeString()} was notified of exception: $e")
    }
}
