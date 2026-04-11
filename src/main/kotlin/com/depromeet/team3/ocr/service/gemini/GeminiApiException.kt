package com.depromeet.team3.ocr.service.gemini

class GeminiApiException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
