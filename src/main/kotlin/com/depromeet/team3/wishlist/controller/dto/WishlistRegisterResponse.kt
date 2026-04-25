package com.depromeet.team3.wishlist.controller.dto

import com.depromeet.team3.product.domain.Product
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
        fun from(wish: Wish, product: Product): WishlistRegisterResponse = WishlistRegisterResponse(
            wishId = requireNotNull(wish.id),
            name = product.name,
            regularPrice = product.regularPrice,
            discountedPrice = product.discountedPrice,
            discountRate = product.discountRate,
            currency = product.currency,
            imageUrl = product.imageUrl,
        )
    }
}
