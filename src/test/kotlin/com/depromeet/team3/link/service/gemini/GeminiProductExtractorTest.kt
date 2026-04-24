package com.depromeet.team3.link.service.gemini

import com.depromeet.team3.link.service.ProductExtractionException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull

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
    fun `Gemini url_context end-to-end 호출이 살아 있고 응답을 구조화해 돌려준다`() {
        // 외부 URL 이라 Gemini 판단(Product vs notProductPage)이 유동적이다.
        // smoke 수준에서는 두 결과 모두 "url_context fetch + structured output 경로가 정상 동작" 의 증거.
        val url = "https://www.apple.com/shop/buy-iphone/iphone-15"

        runCatching { extractor.extract(url) }
            .fold(
                onSuccess = { product -> assertNotNull(product) },
                onFailure = { e ->
                    require(e is ProductExtractionException) {
                        "예상 외 예외: ${e.javaClass.simpleName} - ${e.message}"
                    }
                },
            )
    }
}
