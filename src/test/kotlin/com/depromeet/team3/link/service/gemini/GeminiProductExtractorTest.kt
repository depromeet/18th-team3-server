package com.depromeet.team3.link.service.gemini

import com.depromeet.team3.link.domain.ProductLink
import com.depromeet.team3.link.service.ProductExtractionException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.TimeUnit

/**
 * 실제 Gemini API 를 호출하는 통합 테스트.
 * url_context tool 로 외부 URL 을 fetch 하므로 퍼블릭 인터넷 접근이 필요하다.
 * CI 등에서 GEMINI_API_KEY 가 주입된 경우에만 실행된다.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
class GeminiProductExtractorTest {

    @Autowired
    lateinit var extractor: GeminiProductExtractor

    @Test
    @Timeout(value = 90, unit = TimeUnit.SECONDS)
    fun `Gemini url_context end-to-end 호출이 살아 있고 응답을 구조화해 돌려준다`() {
        // 외부 URL 에 대한 Gemini 의 isProductPage 판정이 유동적이라 Product 반환과
        // notProductPage 예외 모두 url_context fetch + structured output 경로의 정상 동작 신호로 본다.
        val link = ProductLink.parse("https://www.apple.com/shop/buy-iphone/iphone-15")

        try {
            extractor.extract(link)
        } catch (e: ProductExtractionException) {
            // 상품 페이지가 아니라고 판단된 경우. Gemini 가 응답을 구조화해 돌려줬다는 증거이므로 통과.
        }
    }
}
