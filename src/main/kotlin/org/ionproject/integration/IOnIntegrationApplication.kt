package org.ionproject.integration

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBatchProcessing
class IOnIntegrationApplication

fun main(args: Array<String>) {
    runApplication<IOnIntegrationApplication>(*args)
}
