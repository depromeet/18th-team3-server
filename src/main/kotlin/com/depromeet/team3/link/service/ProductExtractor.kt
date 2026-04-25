package com.depromeet.team3.link.service

import com.depromeet.team3.common.domain.Product
import java.net.URI

interface ProductExtractor {
    fun extract(url: URI): Product
}
