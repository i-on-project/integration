package org.ionproject.integration.infrastructure.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class JobNotFoundException(val jobId: Long) : IntegrationException("Job with ID $jobId not found")
