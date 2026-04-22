package com.depromeet.team3.link.service.gemini

import com.depromeet.team3.link.service.ProductExtractionException
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class GeminiExtractionResultTest {

    @Test
    fun `isProductPage 가 false 이면 notProductPage 예외를 던진다`() {
        val result = GeminiExtractionResult(
            isProductPage = false,
            name = null,
        )

        assertFailsWith<ProductExtractionException> {
            result.toProduct()
        }
    }

    @Test
    fun `isProductPage 가 true 라도 name 이 비어 있으면 missingName 예외를 던진다`() {
        val result = GeminiExtractionResult(
            isProductPage = true,
            name = "   ",
        )

        assertFailsWith<ProductExtractionException> {
            result.toProduct()
        }
    }

    @Test
    fun `일부 필드만 있어도 Product 로 변환된다`() {
        val result = GeminiExtractionResult(
            isProductPage = true,
            name = "테스트 상품",
            regularPrice = 10_000,
        )

        val product = result.toProduct()

        assertEquals("테스트 상품", product.name)
        assertEquals(10_000, product.regularPrice)
        assertNull(product.discountedPrice)
        assertNull(product.imageUrl)
        assertNull(product.brand)
    }

    @Test
    fun `전 필드가 채워진 경우 그대로 Product 에 매핑된다`() {
        val result = GeminiExtractionResult(
            isProductPage = true,
            name = "나이키 에어포스",
            regularPrice = 139_000,
            discountedPrice = 99_000,
            discountRate = 28,
            currency = "KRW",
            imageUrl = "https://cdn.example.com/p/42.jpg",
            brand = "Nike",
            category = "신발",
        )

        val product = result.toProduct()

        assertEquals("나이키 에어포스", product.name)
        assertEquals(139_000, product.regularPrice)
        assertEquals(99_000, product.discountedPrice)
        assertEquals(28, product.discountRate)
        assertEquals("KRW", product.currency)
        assertEquals("https://cdn.example.com/p/42.jpg", product.imageUrl)
        assertEquals("Nike", product.brand)
        assertEquals("신발", product.category)
    }

    @Test
    fun `비어 있는 부가 필드 문자열은 null 로 정규화된다`() {
        val result = GeminiExtractionResult(
            isProductPage = true,
            name = "테스트",
            imageUrl = "",
            brand = "   ",
            category = "",
        )

        val product = result.toProduct()

        assertNull(product.imageUrl)
        assertNull(product.brand)
        assertNull(product.category)
    }
}
