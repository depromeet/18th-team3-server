package com.depromeet.team3.common.exception

abstract class BaseException(
    override val message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    companion object {
        fun detailOrDefault(detail: String?): String = detail ?: "원인 불명"
    }
}
