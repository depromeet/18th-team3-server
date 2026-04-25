package com.depromeet.team3.wishlist.service

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.link.domain.ProductLink
import com.depromeet.team3.link.service.ProductExtractor
import com.depromeet.team3.product.repository.ProductJpaRepository
import com.depromeet.team3.support.IntegrationTestSupport
import com.depromeet.team3.wishlist.repository.WishlistJpaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
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
        var stubProduct: Product = Product(name = "기본 상품")
        override fun extract(link: ProductLink): Product = stubProduct
    }

    @Autowired
    private lateinit var wishlistService: WishlistService

    @Autowired
    private lateinit var productJpaRepository: ProductJpaRepository

    @Autowired
    private lateinit var wishlistJpaRepository: WishlistJpaRepository

    @Autowired
    private lateinit var stubExtractor: StubProductExtractor

    @BeforeEach
    fun cleanUp() {
        wishlistJpaRepository.deleteAll()
        productJpaRepository.deleteAll()
        stubExtractor.stubProduct = Product(name = "기본 상품")
    }

    @Test
    fun `정상 등록 - product 와 wishlist 가 모두 저장된다`() {
        val url = "https://shop.example.com/products/42"
        stubExtractor.stubProduct = Product(
            name = "나이키 에어포스",
            regularPrice = 139_000,
            discountedPrice = 99_000,
            currency = "KRW",
            imageUrl = "https://cdn.example.com/p/42.jpg",
        )

        val saved = wishlistService.register(rawUrl = url, userId = 1L)

        assertNotNull(saved.id)
        assertEquals(1L, saved.userId)
        assertEquals(99_000, saved.snapshotDiscountedPrice)
        assertEquals(139_000, saved.snapshotRegularPrice)

        val product = productJpaRepository.findBySourceUrl(url)
        assertNotNull(product)
        assertEquals("나이키 에어포스", product.name)
    }

    @Test
    fun `같은 유저가 같은 URL 을 두 번 등록하면 WishlistAlreadyExistsException`() {
        val url = "https://shop.example.com/products/42"

        wishlistService.register(rawUrl = url, userId = 1L)

        assertFailsWith<WishlistAlreadyExistsException> {
            wishlistService.register(rawUrl = url, userId = 1L)
        }
    }

    @Test
    fun `다른 유저가 같은 URL 을 등록하면 product 는 재사용되고 wishlist 는 따로 생긴다`() {
        val url = "https://shop.example.com/products/42"

        wishlistService.register(rawUrl = url, userId = 1L)
        wishlistService.register(rawUrl = url, userId = 2L)

        assertEquals(1, productJpaRepository.count())
        assertEquals(2, wishlistJpaRepository.count())
    }
}
