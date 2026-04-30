package com.depromeet.team3.common.response

import com.depromeet.team3.common.exception.ErrorCategory
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

data class ApiResponse<T>(
    val status: Int,
    val data: T?,
    val detail: String,
    val code: String,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val pageResponse: PageResponse? = null,
) {
    companion object {

        fun <T> ok(data: T? = null): ApiResponse<T> = ApiResponse(
            status = HttpStatus.OK.value(),
            data = data,
            detail = "요청이 정상적으로 처리되었습니다.",
            code = "COMMON_SUCCESS",
        )

        fun <T> created(data: T? = null): ApiResponse<T> = ApiResponse(
            status = HttpStatus.CREATED.value(),
            data = data,
            detail = "정상적으로 생성되었습니다.",
            code = "CREATED",
        )

        fun <T> fail(
            category: ErrorCategory,
            status: HttpStatus,
            detail: String? = null,
        ): ApiResponse<T> = ApiResponse(
            status = status.value(),
            data = null,
            detail = detail ?: category.description,
            code = status.name,
        )
    }
}
