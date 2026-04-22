package com.depromeet.team3.common.domain

data class Product(
    val name: String,
    val regularPrice: Int? = null,
    val discountedPrice: Int? = null,
    val discountRate: Int? = null,
    val currency: String? = null,
    val imageUrl: String? = null,
    val brand: String? = null,
    val category: String? = null,
)
