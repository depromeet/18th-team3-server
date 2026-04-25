package com.depromeet.team3.wishlist.controller.dto

import java.util.UUID

data class WishlistRegisterRequest(
    val url: String,
    val guestId: UUID,
)
