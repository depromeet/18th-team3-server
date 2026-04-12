package com.depromeet.team3.ocr.service.gemini

import com.depromeet.team3.common.exception.BaseException
import com.depromeet.team3.common.exception.ErrorCategory
import com.depromeet.team3.common.exception.HttpMappable
import org.springframework.http.HttpStatus

class GeminiApiException private constructor(
    message: String,
    override val category: ErrorCategory,
    cause: Throwable? = null,
) : BaseException(message, cause), HttpMappable {

    override val httpStatus: HttpStatus = HttpStatus.BAD_GATEWAY

    companion object {
        fun upstreamError(detail: String?, cause: Throwable): GeminiApiException =
            GeminiApiException("Gemini 호출 실패: ${detailOrDefault(detail)}", ErrorCategory.RETRYABLE, cause)

        fun emptyResponse(): GeminiApiException =
            GeminiApiException("Gemini 응답이 비어 있습니다.", ErrorCategory.RETRYABLE)

        fun parseError(detail: String?, cause: Throwable): GeminiApiException =
            GeminiApiException("Gemini 응답 처리 실패: ${detailOrDefault(detail)}", ErrorCategory.SERVER_ERROR, cause)

        fun noTextPart(): GeminiApiException =
            GeminiApiException("Gemini 응답에 텍스트 파트가 없습니다.", ErrorCategory.SERVER_ERROR)
    }
}
