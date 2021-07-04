package org.ionproject.integration.ui.controller

import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.exception.JobNotFoundException
import org.ionproject.integration.ui.dto.Problem
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ControllerConfig : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [ArgumentException::class])
    fun handle(exception: ArgumentException, request: WebRequest): ResponseEntity<Problem> {
        logger.error("Error processing request $request: ${exception.message}")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(Problem.of(exception, (request as ServletWebRequest).request))
    }

    @ExceptionHandler(value = [JobNotFoundException::class])
    fun handleNotFound(exception: JobNotFoundException, request: WebRequest): ResponseEntity<Problem> {
        logger.error("Error processing request $request: ${exception.message}")

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(Problem.of(exception, (request as ServletWebRequest).request))
    }
}
