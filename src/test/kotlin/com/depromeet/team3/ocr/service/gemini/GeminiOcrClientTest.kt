package com.depromeet.team3.ocr.service.gemini

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

//@Disabled("실제 Gemini API를 호출하는 테스트. API 키 설정 후 수동 실행.")
// NOTE: Gemini API 가 간헐적으로 503 을 반환해 이 테스트가 불안정할 수 있음.
//       프로덕션 코드에 재시도 로직이 들어가면 이 테스트도 재시도를 검증하도록 보강할 것.
@SpringBootTest
class GeminiOcrClientTest(
    @Autowired private val geminiOcrClient: GeminiOcrClient,
) {

    @Test
    fun `이미지에서 상품 정보를 추출한다`() {
        val imageBytes = javaClass.getResourceAsStream("/test-product.png")?.readAllBytes()
            ?: throw IllegalStateException("src/test/resources/test-product.png 파일을 준비해주세요.")

        val product = geminiOcrClient.analyzeImage(imageBytes, "image/png")

        println("=== OCR 결과 ===")
        println("상품명: ${product.name}")
        println("가격: ${product.price}")
        println("카테고리: ${product.category}")
    }
}
