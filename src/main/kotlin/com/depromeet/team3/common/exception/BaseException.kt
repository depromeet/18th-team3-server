package com.depromeet.team3.common.exception

abstract class BaseException(
    override val message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
