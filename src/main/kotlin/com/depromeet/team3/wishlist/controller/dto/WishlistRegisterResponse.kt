package com.depromeet.team3.wishlist.controller.dto

import com.depromeet.team3.wishlist.domain.Wish

data class WishlistRegisterResponse(
    val wishId: Long,
    val name: String?,
    val regularPrice: Int?,
    val discountedPrice: Int?,
    val discountRate: Int?,
    val currency: String?,
    val imageUrl: String?,
) {
    companion object {
        fun from(wish: Wish): WishlistRegisterResponse = WishlistRegisterResponse(
            wishId = wish.getId(),
            name = wish.product.name,
            regularPrice = wish.product.regularPrice,
            discountedPrice = wish.product.discountedPrice,
            discountRate = wish.product.discountRate,
            currency = wish.product.currency,
            imageUrl = wish.product.imageUrl,
        )
    }
}
