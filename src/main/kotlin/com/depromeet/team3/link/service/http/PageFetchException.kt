package com.depromeet.team3.link.service.http

import com.depromeet.team3.common.exception.BaseException
import com.depromeet.team3.common.exception.ErrorCategory
import com.depromeet.team3.common.exception.HttpMappable
import org.springframework.http.HttpStatus

class PageFetchException private constructor(
    message: String,
    override val category: ErrorCategory,
    override val httpStatus: HttpStatus,
    cause: Throwable? = null,
) : BaseException(message, cause), HttpMappable {

    companion object {
        // 대상 페이지 서버가 5xx 또는 연결 실패. 재시도로 복구 가능성 있음.
        fun upstreamError(url: String, cause: Throwable): PageFetchException =
            PageFetchException(
                "링크 페이지 호출 실패: $url",
                ErrorCategory.RETRYABLE,
                HttpStatus.BAD_GATEWAY,
                cause,
            )

        // 4xx (404, 403 로그인 벽, 410 등). 입력 URL 자체가 문제이므로 사용자에게 400 으로 노출.
        fun clientError(url: String, cause: Throwable): PageFetchException =
            PageFetchException(
                "링크 페이지에 접근할 수 없습니다: $url",
                ErrorCategory.INVALID_INPUT,
                HttpStatus.BAD_REQUEST,
                cause,
            )

        fun emptyBody(url: String): PageFetchException =
            PageFetchException(
                "링크 페이지 응답이 비어 있습니다: $url",
                ErrorCategory.RETRYABLE,
                HttpStatus.BAD_GATEWAY,
            )
    }
}
