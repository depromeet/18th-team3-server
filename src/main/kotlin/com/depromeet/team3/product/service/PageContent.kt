package com.depromeet.team3.product.service

import com.depromeet.team3.product.domain.ProductLink

data class PageContent(
    val link: ProductLink,
    val html: String,
)
