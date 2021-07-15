package org.ionproject.integration.ui.output

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.exception.IntegrationException
import org.ionproject.integration.infrastructure.exception.InvalidTokenException
import org.ionproject.integration.infrastructure.exception.JobNotFoundException
import org.ionproject.integration.infrastructure.exception.TokenMissingException
import org.springframework.http.HttpStatus
import javax.servlet.http.HttpServletRequest

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Problem(
    @JsonIgnore
    val httpStatus: HttpStatus,
    val type: String,
    val title: String = httpStatus.reasonPhrase,
    val status: Int? = httpStatus.value(),
    val detail: String? = null,
    val instance: String? = null
) {
    companion object Factory {
        fun of(exception: IntegrationException, request: HttpServletRequest): Problem =
            when (exception) {
                is ArgumentException -> generateProblem(exception, HttpStatus.BAD_REQUEST, request)
                is InvalidTokenException -> generateProblem(exception, HttpStatus.FORBIDDEN, request)
                is JobNotFoundException -> generateProblem(exception, HttpStatus.NOT_FOUND, request)
                is TokenMissingException -> generateProblem(exception, HttpStatus.UNAUTHORIZED, request)
            }

        private fun generateProblem(
            exception: IntegrationException,
            httpStatus: HttpStatus,
            request: HttpServletRequest
        ): Problem {
            return Problem(
                httpStatus = httpStatus,
                detail = exception.message,
                type = exception.definitionUri.toString(),
                instance = request.requestURI
            )
        }
    }
}
