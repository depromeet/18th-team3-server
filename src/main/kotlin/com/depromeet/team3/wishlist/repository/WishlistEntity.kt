package com.depromeet.team3.wishlist.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "wishlists")
class WishlistEntity(
    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "product_id", nullable = false)
    var productId: Long,

    @Column(name = "snapshot_regular_price")
    var snapshotRegularPrice: Int? = null,

    @Column(name = "snapshot_discounted_price")
    var snapshotDiscountedPrice: Int? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: Instant
}
