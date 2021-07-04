package org.ionproject.integration.ui.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.exception.JobNotFoundException
import org.springframework.http.HttpStatus
import javax.servlet.http.HttpServletRequest

private const val ARGUMENT_EX_URI = "https://github.com/i-on-project/integration/blob/master/docs/infrastructure/ArgumentException.md"
private const val JOB_NOT_FOUND_EX_URI = "https://github.com/i-on-project/integration/blob/master/docs/infrastructure/JobNotFoundException.md"

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Problem(
    val type: String,
    val title: String,
    val status: Int? = null,
    val detail: String? = null,
    val instance: String? = null
) {
    companion object Factory {
        fun of(argumentException: ArgumentException, request: HttpServletRequest): Problem =
            Problem(
                title = HttpStatus.BAD_REQUEST.reasonPhrase,
                status = HttpStatus.BAD_REQUEST.value(),
                detail = argumentException.message,
                type = ARGUMENT_EX_URI,
                instance = request.requestURI
            )

        fun of(jobNotFoundException: JobNotFoundException, request: HttpServletRequest): Problem {
            return Problem(
                title = HttpStatus.NOT_FOUND.reasonPhrase,
                status = HttpStatus.NOT_FOUND.value(),
                detail = jobNotFoundException.message,
                type = JOB_NOT_FOUND_EX_URI,
                instance = request.requestURI
            )
        }
    }
}
