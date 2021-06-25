package org.ionproject.integration.ui.controller

import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.exception.JobNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ControllerConfig : ResponseEntityExceptionHandler() {

    // TODO: Use json+problem (?)
    @ExceptionHandler(value = [ArgumentException::class])
    fun handle(exception: ArgumentException, request: WebRequest): ResponseEntity<Any> {
        logger.error("Error processing request $request: ${exception.message}")
        return handleExceptionInternal(exception, exception.message, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(value = [JobNotFoundException::class])
    fun handleNotFound(exception: JobNotFoundException, request: WebRequest): ResponseEntity<Any> {
        logger.error("Error processing request $request: ${exception.message}")
        return handleExceptionInternal(exception, exception.message, HttpHeaders(), HttpStatus.NOT_FOUND, request)
    }
}
