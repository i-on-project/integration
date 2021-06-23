package org.ionproject.integration.ui.controller

import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ControllerConfig : ResponseEntityExceptionHandler() {
    private val logger = LoggerFactory.getLogger(ControllerConfig::class.java)

    // TODO: Use json+problem (?)
    @ExceptionHandler(value = [ArgumentException::class])
    fun handle(exception: ArgumentException, request: WebRequest): ResponseEntity<Any> {
        logger.error("Error processing request $request: ${exception.message}")
        return handleExceptionInternal(exception, exception.message, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }
}
