package org.ionproject.integration.ui.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import javax.servlet.http.HttpServletResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
class PostResponse(
    val location: String? = null,
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
