package org.ionproject.integration.infrastructure.exception

import org.ionproject.integration.application.config.PROJECT_DOCUMENTATION_ROOT
import java.net.URI

private const val URI = "${PROJECT_DOCUMENTATION_ROOT}JobNotFoundException.md"

class JobNotFoundException(val jobId: Long) : IntegrationException("Job with ID $jobId not found") {
    override val definitionUri: URI
        get() = URI(URI)
}
