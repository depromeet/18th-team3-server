package com.depromeet.team3.wishlist.service

import com.depromeet.team3.common.exception.BaseException
import com.depromeet.team3.common.exception.ErrorCategory
import com.depromeet.team3.common.exception.HttpMappable
import com.depromeet.team3.product.domain.ProductLink
import org.springframework.http.HttpStatus
import java.util.UUID

class WishAlreadyExistsException(
    guestId: UUID,
    link: ProductLink,
) : BaseException("이미 위시리스트에 등록된 상품입니다. guestId=$guestId link=$link"), HttpMappable {
    override val httpStatus: HttpStatus = HttpStatus.CONFLICT
    override val category: ErrorCategory = ErrorCategory.CONFLICT
}
