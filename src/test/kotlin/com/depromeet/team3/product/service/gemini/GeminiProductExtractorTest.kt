package com.depromeet.team3.product.service.gemini

import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.product.service.ProductExtractionException
import com.depromeet.team3.support.IntegrationTestSupport
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.TimeUnit
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 실제 Gemini API 를 호출하는 통합 테스트.
 *
 * 비용·외부 의존성이 있으므로 기본은 @Disabled. 호출 경로 검증이 필요할 때만 명시적으로 enable.
 * GEMINI_API_KEY 가 환경에 있다고 가정한다.
 */
@Disabled("실제 Gemini API 호출. 검증 필요 시 수동으로 enable 후 실행.")
class GeminiProductExtractorTest : IntegrationTestSupport() {

    @Autowired
    lateinit var extractor: GeminiProductExtractor

    @Test
    @Timeout(value = 90, unit = TimeUnit.SECONDS)
    fun `Gemini end-to-end 호출이 살아 있고 응답을 구조화해 돌려준다`() {
        // Gemini 가 외부 URL 을 상품 페이지로 판정하면 Product 가, 아니면 ProductExtractionException
        // 만 받아 정상 신호로 간주한다. 둘 다 정적 fetch + structured output 경로가 살아 있다는 증거.
        // 그 외 다른 예외는 호출 경로(스키마, 인증, 직렬화 등)가 깨졌다는 뜻이라 fail 시켜야 한다.
        val link = ProductLink.parse("https://www.apple.com/shop/buy-iphone")

        val result = runCatching { extractor.extract(link) }
        result.fold(
            onSuccess = { product ->
                assertNotNull(product, "extract 결과가 null 일 수 없음")
            },
            onFailure = { e ->
                assertTrue(
                    e is ProductExtractionException,
                    "허용되는 실패는 ProductExtractionException 한정. 실제: ${e.javaClass.simpleName}: ${e.message}",
                )
            },
        )
    }
}
