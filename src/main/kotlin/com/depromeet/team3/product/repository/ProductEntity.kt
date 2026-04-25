package com.depromeet.team3.product.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "products")
class ProductEntity(
    @Column(name = "source_url", nullable = false, length = 2048)
    var sourceUrl: String,

    @Column(name = "name", length = 512)
    var name: String? = null,

    @Column(name = "regular_price")
    var regularPrice: Int? = null,

    @Column(name = "discounted_price")
    var discountedPrice: Int? = null,

    @Column(name = "currency", length = 8)
    var currency: String? = null,

    @Column(name = "image_url", length = 2048)
    var imageUrl: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: Instant
}
