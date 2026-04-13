package com.depromeet.team3.common.exception

import org.springframework.http.HttpStatus

interface HttpMappable {
    val httpStatus: HttpStatus
    val category: ErrorCategory
}
