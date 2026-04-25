package com.depromeet.team3.wishlist.repository

import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.wishlist.domain.Wish
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WishJpaRepository : JpaRepository<Wish, Long> {
    fun existsByGuestIdAndProductLink(guestId: UUID, link: ProductLink): Boolean
}
