package com.depromeet.team3.wishlist.service

import com.depromeet.team3.product.domain.Product
import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.product.repository.ProductJpaRepository
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
        var build: (ProductLink) -> Product = { Product(sourceUrl = it.toString(), name = "기본 상품") }
        override fun extract(link: ProductLink): Product = build(link)
    }

    @Autowired
    private lateinit var wishlistService: WishlistService

    @Autowired
    private lateinit var productJpaRepository: ProductJpaRepository

    @Autowired
    private lateinit var wishJpaRepository: WishJpaRepository

    @Autowired
    private lateinit var stubExtractor: StubProductExtractor

    @BeforeEach
    fun cleanUp() {
        wishJpaRepository.deleteAll()
        productJpaRepository.deleteAll()
        stubExtractor.build = { Product(sourceUrl = it.toString(), name = "기본 상품") }
    }

    @Test
    fun `정상 등록 - product 와 wish 가 모두 저장된다`() {
        val url = "https://shop.example.com/products/42"
        val guestId = UUID.randomUUID()
        stubExtractor.build = { link ->
            Product(
                sourceUrl = link.toString(),
                name = "나이키 에어포스",
                regularPrice = 139_000,
                discountedPrice = 99_000,
                currency = "KRW",
                imageUrl = "https://cdn.example.com/p/42.jpg",
            )
        }

        val result = wishlistService.register(rawUrl = url, guestId = guestId)

        assertNotNull(result.wish.id)
        assertEquals(guestId, result.wish.guestId)
        assertEquals(99_000, result.wish.snapshotDiscountedPrice)
        assertEquals(139_000, result.wish.snapshotRegularPrice)
        assertEquals("나이키 에어포스", result.product.name)

        val product = productJpaRepository.findBySourceUrl(url)
        assertNotNull(product)
        assertEquals("나이키 에어포스", product.name)
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
    fun `다른 guest 가 같은 URL 을 등록하면 product 는 재사용되고 wish 는 따로 생긴다`() {
        val url = "https://shop.example.com/products/42"

        wishlistService.register(rawUrl = url, guestId = UUID.randomUUID())
        wishlistService.register(rawUrl = url, guestId = UUID.randomUUID())

        assertEquals(1, productJpaRepository.count())
        assertEquals(2, wishJpaRepository.count())
    }
}
