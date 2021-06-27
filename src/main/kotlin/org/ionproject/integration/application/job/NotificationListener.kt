package org.ionproject.integration.application.job

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener

class NotificationListener : JobExecutionListener {

    private val log: Logger = LoggerFactory.getLogger(NotificationListener::class.java)

    override fun beforeJob(jobExecution: JobExecution) {
        log.info("Job ${jobExecution.jobConfigurationName} starting")
    }

    override fun afterJob(jobExecution: JobExecution) {
        when (jobExecution.exitStatus) {
            ExitStatus.FAILED -> log.error("Job ${jobExecution.jobConfigurationName} failed")
            ExitStatus.COMPLETED -> log.info("Job ${jobExecution.jobConfigurationName} completed")
            else -> log.debug("Job ${jobExecution.jobConfigurationName} exited with status = ${jobExecution.status}")
        }
    }
}
