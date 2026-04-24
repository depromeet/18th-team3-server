package com.depromeet.team3.link.service

import com.depromeet.team3.common.domain.Product

interface ProductExtractor {
    fun extract(url: String): Product
}
