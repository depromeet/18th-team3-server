package com.depromeet.team3.link.domain

import java.net.URI

@JvmInline
value class ProductLink private constructor(val value: URI) {
    override fun toString(): String = value.toString()

    companion object {
        private val HTTP_SCHEMES = setOf("http", "https")

        fun parse(raw: String): ProductLink {
            val trimmed = raw.trim()
            require(trimmed.isNotBlank()) { "URL이 비어 있습니다." }
            val uri = try {
                URI.create(trimmed)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("유효한 URL 형식이 아닙니다: $trimmed", e)
            }
            // URI.create 는 스킴 없는 "example.com/product" 도 relative URI 로 통과시키므로 명시 검증.
            require(uri.scheme in HTTP_SCHEMES) { "http/https URL만 허용합니다." }
            return ProductLink(uri)
        }
    }
}
