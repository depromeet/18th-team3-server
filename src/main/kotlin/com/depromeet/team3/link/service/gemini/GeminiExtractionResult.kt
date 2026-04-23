package com.depromeet.team3.link.service.gemini

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.link.service.ProductExtractionException

data class GeminiExtractionResult(
    val isProductPage: Boolean,
    val name: String? = null,
    val regularPrice: Int? = null,
    val discountedPrice: Int? = null,
    val currency: String? = null,
    val imageUrl: String? = null,
) {
    fun toProduct(): Product {
        if (!isProductPage) throw ProductExtractionException.notProductPage()
        return Product(
            name = name?.takeIf { it.isNotBlank() },
            regularPrice = regularPrice,
            discountedPrice = discountedPrice,
            currency = currency,
            imageUrl = imageUrl?.takeIf { it.isNotBlank() },
        )
    }
}
