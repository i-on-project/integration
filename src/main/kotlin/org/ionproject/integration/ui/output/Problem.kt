package org.ionproject.integration.ui.output

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.exception.IntegrationException
import org.ionproject.integration.infrastructure.exception.InvalidTokenException
import org.ionproject.integration.infrastructure.exception.JobNotFoundException
import org.ionproject.integration.infrastructure.exception.TokenMissingException
import org.springframework.http.HttpStatus
import java.net.URI
import java.net.URL
import javax.servlet.http.HttpServletRequest

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Problem(
    @JsonIgnore
    val httpStatus: HttpStatus,

    @Schema(
        description = "URL to a page with more details about the problem.",
        implementation = URL::class,
        example = "https://github.com/i-on-project/integration/blob/master/docs/infrastructure/ArgumentException.md"
    )
    val type: String,

    @Schema(
        description = "Short human-readable summary of the problem.",
        example = "Invalid or missing argument"
    )
    val title: String,

    @Schema(
        description = "HTTP status code.",
        example = "400",
        implementation = HttpStatus::class
    )
    val status: Int? = httpStatus.value(),

    @Schema(
        description = "Human-readable description of this specific problem.",
        example = "Job with ID 111 does not exist"
    )
    val detail: String? = null,

    @Schema(
        description = "URI that describes where the problem occured.",
        example = "/integration/jobs/111",
        implementation = URI::class
    )
    val instance: String? = null
) {
    companion object Factory {
        fun of(exception: IntegrationException, request: HttpServletRequest): Problem {
            fun generateProblem(httpStatus: HttpStatus): Problem = Problem(
                httpStatus = httpStatus,
                title = exception.title,
                detail = exception.message,
                type = exception.definitionUri.toString(),
                instance = request.requestURI
            )

            return when (exception) {
                is ArgumentException -> generateProblem(HttpStatus.BAD_REQUEST)
                is InvalidTokenException -> generateProblem(HttpStatus.FORBIDDEN)
                is JobNotFoundException -> generateProblem(HttpStatus.NOT_FOUND)
                is TokenMissingException -> generateProblem(HttpStatus.UNAUTHORIZED)
            }
        }
    }
}
