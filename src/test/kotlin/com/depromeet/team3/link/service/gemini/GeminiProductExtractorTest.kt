package com.depromeet.team3.link.service.gemini

import com.depromeet.team3.link.service.PageContent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

/**
 * 실제 Gemini API 를 호출하는 통합 테스트.
 * CI 등에서 GEMINI_API_KEY 가 주입된 경우에만 실행된다.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
class GeminiProductExtractorTest {

    @Autowired
    lateinit var extractor: GeminiProductExtractor

    @Test
    fun `OG 태그 기반 최소 상품 HTML 에서 상품명과 이미지 URL 을 뽑는다`() {
        val html = """
            <!DOCTYPE html>
            <html lang="ko">
              <head>
                <meta property="og:title" content="테스트 우유 1L" />
                <meta property="og:image" content="https://cdn.example.com/test-milk.jpg" />
                <meta property="og:type" content="product" />
              </head>
              <body>
                <h1>테스트 우유 1L</h1>
                <div class="price">3,200원</div>
                <div class="brand">서울우유</div>
                <nav class="breadcrumb">식품 &gt; 유제품 &gt; 우유</nav>
              </body>
            </html>
        """.trimIndent()

        val product = extractor.extract(
            PageContent(url = "https://example.com/products/test-milk", html = html),
        )

        assertEquals("테스트 우유 1L", product.name)
        assertEquals(3_200, product.regularPrice)
        assertEquals("KRW", product.currency)
    }
}
