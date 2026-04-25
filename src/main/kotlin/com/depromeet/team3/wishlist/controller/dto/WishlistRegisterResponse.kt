package com.depromeet.team3.wishlist.controller.dto

import com.depromeet.team3.wishlist.repository.WishlistEntity
import java.time.Instant

data class WishlistRegisterResponse(
    val wishlistId: Long,
    val userId: Long,
    val productId: Long,
    val snapshotRegularPrice: Int?,
    val snapshotDiscountedPrice: Int?,
    val createdAt: Instant,
) {
    companion object {
        fun from(entity: WishlistEntity): WishlistRegisterResponse = WishlistRegisterResponse(
            wishlistId = requireNotNull(entity.id),
            userId = entity.userId,
            productId = entity.productId,
            snapshotRegularPrice = entity.snapshotRegularPrice,
            snapshotDiscountedPrice = entity.snapshotDiscountedPrice,
            createdAt = entity.createdAt,
        )
    }
}
