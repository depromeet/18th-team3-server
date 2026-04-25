package com.depromeet.team3.wishlist.service.dto

import com.depromeet.team3.product.domain.Product
import com.depromeet.team3.wishlist.domain.Wish

data class WishRegisterResult(
    val wish: Wish,
    val product: Product,
)
