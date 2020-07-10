package org.ionproject.integration.step.chunkbased

import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.job.Generic
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        Generic::class,
        IOnIntegrationApplication::class
    ]
)
@SpringBatchTest
@TestPropertySource("classpath:application.properties")
internal class GenericParseAndUploadToCoreStepTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils
    @Test
    fun test() {
        val jp = JobParametersBuilder().toJobParameters()
        val ec = SpringBatchTestUtils().createExecutionContext()
        // val e = jobLauncherTestUtils.launchStep("Generic Batch Job",jp,ec)
    }

    // well
    // fails on reader:
        // enum does not exist
        // path does not exist
        // path does not comply
        // see that file-hash
    // fails on processor ?
    // fails on writer
        // - what could go wrong?
}
