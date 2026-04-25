package com.depromeet.team3.link.service

import com.depromeet.team3.common.domain.Product
import org.junit.jupiter.api.Test
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LinkServiceTest {

    private val stubExtractor = object : ProductExtractor {
        var lastUrl: URI? = null
        var stubbedProduct: Product = Product(name = "우유")
        override fun extract(url: URI): Product {
            lastUrl = url
            return stubbedProduct
        }
    }

    private val linkService = LinkService(stubExtractor)

    @Test
    fun `URL 이 비어 있으면 IllegalArgumentException 을 던진다`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            linkService.register("   ")
        }
        assertEquals("URL이 비어 있습니다.", ex.message)
    }

    @Test
    fun `http 또는 https 가 아닌 스킴은 거부한다`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            linkService.register("ftp://example.com/product")
        }
        assertEquals("http/https URL만 허용합니다.", ex.message)
    }

    @Test
    fun `스킴 없이 호스트만 들어와도 거부한다`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            linkService.register("example.com/product")
        }
        assertEquals("http/https URL만 허용합니다.", ex.message)
    }

    @Test
    fun `정상 URL 은 Extractor 에 전달되고 결과 Product 를 반환한다`() {
        val raw = "https://shop.example.com/products/42"
        stubExtractor.stubbedProduct = Product(
            name = "나이키 에어포스",
            regularPrice = 139_000,
            discountedPrice = 99_000,
            currency = "KRW",
            imageUrl = "https://cdn.example.com/p/42.jpg",
        )

        val product = linkService.register(raw)

        assertEquals(URI.create(raw), stubExtractor.lastUrl)
        assertEquals("나이키 에어포스", product.name)
        assertEquals(99_000, product.discountedPrice)
    }

    @Test
    fun `앞뒤 공백은 제거한 채 Extractor 에 전달된다`() {
        val raw = "https://shop.example.com/products/42"

        linkService.register("  $raw  ")

        assertEquals(URI.create(raw), stubExtractor.lastUrl)
    }
}
