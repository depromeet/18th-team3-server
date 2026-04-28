package com.depromeet.team3.common.exception

import com.depromeet.team3.common.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("[{}] {}", e.javaClass.simpleName, e.message, e)
        val status = if (e is HttpMappable) e.httpStatus else HttpStatus.INTERNAL_SERVER_ERROR
        val category = if (e is HttpMappable) e.category else ErrorCategory.SERVER_ERROR
        return ResponseEntity
            .status(status)
            .body(ApiResponse.fail(category, status, e.message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("[IllegalArgumentException] {}", e.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(ErrorCategory.INVALID_INPUT, HttpStatus.BAD_REQUEST))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("[UnexpectedException] {}", e.message, e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail(ErrorCategory.SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR))
    }
}
