package org.ionproject.integration.ui.config

import org.ionproject.integration.infrastructure.exception.IntegrationException
import org.ionproject.integration.infrastructure.exception.TokenMissingException
import org.ionproject.integration.ui.output.Problem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class ControllerConfig : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [IntegrationException::class])
    fun handleIntegrationException(exception: IntegrationException, request: WebRequest): ResponseEntity<Problem> {
        logError(request, exception)

        val problem = Problem.of(exception, (request as ServletWebRequest).request)

        return ResponseEntity
            .status(problem.httpStatus)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem)
    }

    @ExceptionHandler(value = [TokenMissingException::class])
    fun handleTokenMissingException(exception: TokenMissingException, request: WebRequest): ResponseEntity<Problem> {
        logError(request, exception)

        val problem = Problem.of(exception, (request as ServletWebRequest).request)

        return ResponseEntity
            .status(problem.httpStatus)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem)
    }

    private fun logError(request: WebRequest, exception: IntegrationException) {
        logger.error("Error processing request $request: ${exception.message}")
    }
}

@Component
class FilterChainExceptionHandler : OncePerRequestFilter() {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private lateinit var resolver: HandlerExceptionResolver

    @Throws(ServletException::class, IntegrationException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            resolver.resolveException(request, response, null, e)
        }
    }
}
