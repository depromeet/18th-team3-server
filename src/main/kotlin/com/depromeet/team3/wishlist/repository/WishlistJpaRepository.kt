package com.depromeet.team3.wishlist.repository

import org.springframework.data.jpa.repository.JpaRepository

interface WishlistJpaRepository : JpaRepository<WishlistEntity, Long> {
    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean
}
