package com.depromeet.team3.product.domain

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
class Product(
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

    // discountRate 는 원가와 할인가에서 파생되는 결정적 값이므로 LLM 이 아닌 서버에서 계산한다.
    val discountRate: Int?
        get() {
            val regular = regularPrice ?: return null
            val discounted = discountedPrice ?: return null
            if (regular <= 0 || discounted >= regular) return null
            return ((regular - discounted) * 100.0 / regular).toInt()
        }
}
