package com.depromeet.team3.link.service

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.link.domain.ProductLink

data class ExtractedLink(
    val link: ProductLink,
    val product: Product,
)
