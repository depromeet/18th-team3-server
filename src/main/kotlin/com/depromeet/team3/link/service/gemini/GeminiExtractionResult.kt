package com.depromeet.team3.link.service.gemini

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.link.service.ProductExtractionException

data class GeminiExtractionResult(
    val isProductPage: Boolean,
    val name: String? = null,
    val regularPrice: Int? = null,
    val discountedPrice: Int? = null,
    val discountRate: Int? = null,
    val currency: String? = null,
    val imageUrl: String? = null,
    val brand: String? = null,
    val category: String? = null,
) {
    fun toProduct(): Product {
        if (!isProductPage) throw ProductExtractionException.notProductPage()
        val productName = name?.takeIf { it.isNotBlank() }
            ?: throw ProductExtractionException.missingName()
        return Product(
            name = productName,
            regularPrice = regularPrice,
            discountedPrice = discountedPrice,
            discountRate = discountRate,
            currency = currency,
            imageUrl = imageUrl?.takeIf { it.isNotBlank() },
            brand = brand?.takeIf { it.isNotBlank() },
            category = category?.takeIf { it.isNotBlank() },
        )
    }
}
