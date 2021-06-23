package org.ionproject.integration.application.config

import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

const val LAUNCHER_NAME = "asyncLauncher"

@Configuration
class BatchConfig {

    @Autowired
    lateinit var jobRepository: JobRepository

    /**
     * Configure an asynchronous Job Launcher
     */
    @Bean(name = [LAUNCHER_NAME])
    fun getAsyncLauncher(): JobLauncher = SimpleJobLauncher().apply {
        setJobRepository(jobRepository)

        val executor = ThreadPoolTaskExecutor().apply {
            maxPoolSize = 1 // Single threaded executor
            initialize()
        }
        setTaskExecutor(executor)

        afterPropertiesSet()
    }
}
