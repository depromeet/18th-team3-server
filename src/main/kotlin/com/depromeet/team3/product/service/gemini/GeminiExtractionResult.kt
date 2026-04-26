package com.depromeet.team3.product.service.gemini

import com.depromeet.team3.product.domain.Product
import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.product.service.ProductExtractionException

data class GeminiExtractionResult(
    val isProductPage: Boolean,
    val name: String? = null,
    val regularPrice: Int? = null,
    val discountedPrice: Int? = null,
    val currency: String? = null,
    val imageUrl: String? = null,
) {
    fun toProduct(link: ProductLink): Product {
        if (!isProductPage) throw ProductExtractionException.notProductPage()
        return Product(
            link = link,
            name = name?.takeIf { it.isNotBlank() },
            imageUrl = imageUrl?.takeIf { it.isNotBlank() },
            regularPrice = regularPrice,
            discountedPrice = discountedPrice,
            currency = currency,
        )
    }
}
