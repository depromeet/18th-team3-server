package com.depromeet.team3.product.service

import com.depromeet.team3.product.domain.ProductLink

interface PageFetcher {
    fun fetch(link: ProductLink): PageContent
}
