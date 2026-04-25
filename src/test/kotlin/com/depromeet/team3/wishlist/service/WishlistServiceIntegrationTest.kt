package com.depromeet.team3.wishlist.service

import com.depromeet.team3.product.domain.Product
import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.product.service.ProductExtractor
import com.depromeet.team3.support.IntegrationTestSupport
import com.depromeet.team3.wishlist.repository.WishJpaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@Import(WishlistServiceIntegrationTest.StubExtractorConfig::class)
class WishlistServiceIntegrationTest : IntegrationTestSupport() {

    @TestConfiguration
    class StubExtractorConfig {
        @Bean
        fun productExtractor(): StubProductExtractor = StubProductExtractor()
    }

    class StubProductExtractor : ProductExtractor {
        var build: (ProductLink) -> Product = { Product(link = it, name = "기본 상품") }
        override fun extract(link: ProductLink): Product = build(link)
    }

    @Autowired
    private lateinit var wishlistService: WishlistService

    @Autowired
    private lateinit var wishJpaRepository: WishJpaRepository

    @Autowired
    private lateinit var stubExtractor: StubProductExtractor

    @BeforeEach
    fun cleanUp() {
        wishJpaRepository.deleteAll()
        stubExtractor.build = { Product(link = it, name = "기본 상품") }
    }

    @Test
    fun `정상 등록 - wish 가 추출 결과를 박제한 채 저장된다`() {
        val url = "https://shop.example.com/products/42"
        val guestId = UUID.randomUUID()
        stubExtractor.build = { link ->
            Product(
                link = link,
                name = "나이키 에어포스",
                regularPrice = 139_000,
                discountedPrice = 99_000,
                currency = "KRW",
                imageUrl = "https://cdn.example.com/p/42.jpg",
            )
        }

        val result = wishlistService.register(rawUrl = url, guestId = guestId)

        assertNotNull(result.wish.getId())
        assertEquals(guestId, result.wish.guestId)
        assertEquals(ProductLink.parse(url), result.wish.product.link)
        assertEquals("나이키 에어포스", result.wish.product.name)
        assertEquals(139_000, result.wish.product.regularPrice)
        assertEquals(99_000, result.wish.product.discountedPrice)
        assertEquals("KRW", result.wish.product.currency)
        assertEquals("https://cdn.example.com/p/42.jpg", result.wish.product.imageUrl)
        assertEquals(28, result.wish.product.discountRate)
    }

    @Test
    fun `같은 guest 가 같은 URL 을 두 번 등록하면 WishAlreadyExistsException`() {
        val url = "https://shop.example.com/products/42"
        val guestId = UUID.randomUUID()

        wishlistService.register(rawUrl = url, guestId = guestId)

        assertFailsWith<WishAlreadyExistsException> {
            wishlistService.register(rawUrl = url, guestId = guestId)
        }
    }

    @Test
    fun `다른 guest 가 같은 URL 을 등록하면 wish 가 따로 생긴다`() {
        val url = "https://shop.example.com/products/42"

        wishlistService.register(rawUrl = url, guestId = UUID.randomUUID())
        wishlistService.register(rawUrl = url, guestId = UUID.randomUUID())

        assertEquals(2, wishJpaRepository.count())
    }
}
