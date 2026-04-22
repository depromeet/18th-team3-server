package com.depromeet.team3.link.controller.dto

import com.depromeet.team3.common.domain.Product

data class LinkRegisterResponse(
    val name: String,
    val regularPrice: Int?,
    val discountedPrice: Int?,
    val discountRate: Int?,
    val currency: String?,
    val imageUrl: String?,
    val brand: String?,
    val category: String?,
) {
    companion object {
        fun from(product: Product): LinkRegisterResponse = LinkRegisterResponse(
            name = product.name,
            regularPrice = product.regularPrice,
            discountedPrice = product.discountedPrice,
            discountRate = product.discountRate,
            currency = product.currency,
            imageUrl = product.imageUrl,
            brand = product.brand,
            category = product.category,
        )
    }
}
