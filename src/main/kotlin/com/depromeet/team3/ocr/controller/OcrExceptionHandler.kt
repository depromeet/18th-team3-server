package com.depromeet.team3.ocr.controller

import com.depromeet.team3.ocr.service.gemini.GeminiApiException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * OCR 관련 예외를 HTTP 응답으로 매핑한다.
 *
 * - [IllegalArgumentException] : 클라이언트 입력 문제 (mimeType 누락, 지원하지 않는 형식 등) → 400
 * - [GeminiApiException] : 업스트림 장애 (응답 비어 있음, 내부 에러 등) → 502
 */
@Order(0)
@RestControllerAdvice(basePackages = ["com.depromeet.team3.ocr"])
class OcrExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(code = "INVALID_REQUEST", message = e.message ?: "잘못된 요청입니다."))

    @ExceptionHandler(GeminiApiException::class)
    fun handleGeminiApi(e: GeminiApiException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(ErrorResponse(code = "UPSTREAM_ERROR", message = e.message ?: "OCR 업스트림 장애입니다."))

    data class ErrorResponse(
        val code: String,
        val message: String,
    )
}
