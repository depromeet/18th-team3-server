package com.depromeet.team3.product.repository

import com.depromeet.team3.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<Product, Long> {
    fun findBySourceUrl(sourceUrl: String): Product?
}
