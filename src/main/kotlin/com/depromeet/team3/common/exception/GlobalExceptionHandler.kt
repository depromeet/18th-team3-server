package com.depromeet.team3.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ProblemDetail {
        val status = if (e is HttpMappable) e.httpStatus else HttpStatus.INTERNAL_SERVER_ERROR
        val category = if (e is HttpMappable) e.category else ErrorCategory.SERVER_ERROR
        return ProblemDetail.forStatusAndDetail(status, e.message).apply {
            setProperty("category", category)
        }
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message ?: "잘못된 요청입니다.").apply {
            setProperty("category", ErrorCategory.INVALID_INPUT)
        }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.").apply {
            setProperty("category", ErrorCategory.SERVER_ERROR)
        }
}
