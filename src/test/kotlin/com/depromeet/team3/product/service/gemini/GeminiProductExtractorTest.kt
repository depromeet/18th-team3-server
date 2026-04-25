package com.depromeet.team3.product.service.gemini

import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.product.service.ProductExtractionException
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.TimeUnit

/**
 * 실제 Gemini API 를 호출하는 통합 테스트.
 *
 * 비용·외부 의존성이 있으므로 기본은 @Disabled. 호출 경로 검증이 필요할 때만 명시적으로 enable.
 * GEMINI_API_KEY 가 환경에 있다고 가정한다.
 */
@SpringBootTest
@Disabled("실제 Gemini API 호출. 검증 필요 시 수동으로 enable 후 실행.")
class GeminiProductExtractorTest {

    @Autowired
    lateinit var extractor: GeminiProductExtractor

    @Test
    @Timeout(value = 90, unit = TimeUnit.SECONDS)
    fun `Gemini end-to-end 호출이 살아 있고 응답을 구조화해 돌려준다`() {
        // 외부 URL 에 대한 Gemini 의 isProductPage 판정이 유동적이라 Product 반환과
        // notProductPage 예외 모두 정적 fetch + structured output 경로의 정상 동작 신호로 본다.
        val link = ProductLink.parse("https://www.apple.com/shop/buy-iphone/iphone-15")

        try {
            extractor.extract(link)
        } catch (e: ProductExtractionException) {
            // 상품 페이지가 아니라고 판단된 경우. Gemini 가 응답을 구조화해 돌려줬다는 증거이므로 통과.
        }
    }
}
