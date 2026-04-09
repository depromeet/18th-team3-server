package com.depromeet.team3.ocr.service.gemini

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * 실제 Gemini API 를 호출하는 통합 테스트.
 *
 * GEMINI_API_KEY 환경변수가 설정되어 있을 때만 실행된다.
 * - 로컬: .env 에 키를 넣고 수동 실행
 * - CI: 시크릿에 GEMINI_API_KEY 를 등록해야만 실행됨
 *
 * 이 테스트의 계약은 **"Gemini 호출이 성공하고 응답이 Product 로 역직렬화된다"** 까지다.
 * 어떤 필드가 채워졌는지(name, price, category 등) 는 LLM 응답 품질 영역이라
 * 계약이 아니며, 그걸 단정하면 외부 모델 variance 로 인한 flaky 가 된다.
 *
 * NOTE: Gemini API 가 간헐적으로 503 을 반환해 이 테스트가 불안정할 수 있음.
 *       프로덕션 코드에 재시도 로직이 들어가면 이 테스트도 재시도를 검증하도록 보강할 것.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
class GeminiOcrClientTest(
    @Autowired private val geminiOcrClient: GeminiOcrClient,
) {

    @Test
    fun `이미지에서 상품 정보를 추출한다`() {
        val imageBytes = javaClass
            .getResourceAsStream("/test-product.png")
            ?.readAllBytes()
            ?: throw IllegalStateException("src/test/resources/test-product.png 파일을 준비해주세요.")

        // 호출이 예외 없이 완료되고 Product 가 반환되면 통과.
        // 필드별 단정은 의도적으로 생략 — LLM 응답에 대한 계약이 아님.
        geminiOcrClient.analyzeImage(imageBytes, "image/png")
    }
}
