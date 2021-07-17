package org.ionproject.integration.ui.output

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import java.net.URI
import javax.servlet.http.HttpServletResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
class PostResponse(
    @Schema(
        description = "Location of the newly created Job execution that can be executed to track its progress.",
        implementation = URI::class,
        example = "http://ion-integration-staging.herokuapp.com/integration/jobs/7"
    )
    val location: String? = null,

    @Schema(
        description = "Status of the job creation request.",
        example = "CREATED"
    )
    val status: HttpStatus = HttpStatus.OK,
    response: HttpServletResponse
) {
    init {
        response.status = status.value()
        location?.let {
            response.addHeader("Location", it)
        }
    }
}
