package com.depromeet.team3.link.service

import com.depromeet.team3.common.domain.Product
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LinkServiceTest {

    private val stubFetcher = object : PageFetcher {
        var lastUrl: String? = null
        var stubbedHtml: String = "<html></html>"
        override fun fetch(url: String): PageContent {
            lastUrl = url
            return PageContent(url = url, html = stubbedHtml)
        }
    }

    private val stubExtractor = object : ProductExtractor {
        var lastPage: PageContent? = null
        var stubbedProduct: Product = Product(name = "우유")
        override fun extract(page: PageContent): Product {
            lastPage = page
            return stubbedProduct
        }
    }

    private val linkService = LinkService(stubFetcher, stubExtractor)

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
    fun `정상 URL 은 Fetcher 와 Extractor 를 순서대로 호출하고 결과 Product 를 반환한다`() {
        val url = "https://shop.example.com/products/42"
        stubExtractor.stubbedProduct = Product(
            name = "나이키 에어포스",
            regularPrice = 139_000,
            discountedPrice = 99_000,
            discountRate = 28,
            currency = "KRW",
            imageUrl = "https://cdn.example.com/p/42.jpg",
            brand = "Nike",
            category = "신발",
        )

        val product = linkService.register(url)

        assertEquals(url, stubFetcher.lastUrl)
        assertEquals(url, stubExtractor.lastPage?.url)
        assertEquals("나이키 에어포스", product.name)
        assertEquals(99_000, product.discountedPrice)
    }

    @Test
    fun `앞뒤 공백은 제거한 채 Fetcher 에 전달된다`() {
        val url = "https://shop.example.com/products/42"

        linkService.register("  $url  ")

        assertEquals(url, stubFetcher.lastUrl)
    }
}
