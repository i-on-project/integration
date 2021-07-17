package org.ionproject.integration.infrastructure.exception

import org.ionproject.integration.application.config.PROJECT_DOCUMENTATION_ROOT
import java.net.URI

private const val URI = "${PROJECT_DOCUMENTATION_ROOT}JobNotFoundException.md"

class JobNotFoundException(val jobId: Long) : IntegrationException("Job with ID $jobId does not exist") {
    override val definitionUri: URI
        get() = URI(URI)
    override val title: String
        get() = "Job not found"
}
