package com.depromeet.team3.product.repository

import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<ProductEntity, Long> {
    fun findBySourceUrl(sourceUrl: String): ProductEntity?
}
