package com.depromeet.team3.product.domain

import java.net.URI

class ProductLink private constructor(val value: URI) {
    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductLink) return false
        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()

    companion object {
        private val HTTP_SCHEMES = setOf("https")

        fun parse(raw: String): ProductLink {
            val trimmed = raw.trim()
            require(trimmed.isNotBlank()) { "URL이 비어 있습니다." }
            val uri = try {
                URI.create(trimmed)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("유효한 URL 형식이 아닙니다: $trimmed", e)
            }
            // URI.create 는 스킴 없는 "example.com/product" 도 relative URI 로 통과시키므로 명시 검증.
            // RFC 3986 은 scheme 을 case-insensitive 로 정의하므로 비교 전에 lowercase 정규화한다.
            require(uri.scheme?.lowercase() in HTTP_SCHEMES) { "https URL만 허용합니다." }
            return ProductLink(uri)
        }
    }
}
