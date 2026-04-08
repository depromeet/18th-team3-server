package com.depromeet.team3.ocr.service.gemini

import com.depromeet.team3.common.domain.Product.Field
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertTrue

/**
 * 실제 Gemini API 를 호출하는 통합 테스트.
 *
 * GEMINI_API_KEY 환경변수가 설정되어 있을 때만 실행된다.
 * - 로컬: .env 에 키를 넣고 수동 실행
 * - CI: 시크릿에 GEMINI_API_KEY 를 등록해야만 실행됨
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
        val imageBytes = javaClass.getResourceAsStream("/test-product.png")?.readAllBytes()
            ?: throw IllegalStateException("src/test/resources/test-product.png 파일을 준비해주세요.")

        val product = geminiOcrClient.analyzeImage(imageBytes, "image/png")

        // 최소한 하나의 필드는 추출되어야 함 (전부 NotFound 면 OCR 실패로 간주)
        val hasAnyExtractedField = listOf(product.name, product.price, product.category)
            .any { it !is Field.NotFound }
        assertTrue(hasAnyExtractedField, "OCR 결과가 전부 NotFound 입니다. 실제 이미지인지 확인하세요.")

        // name 은 상품 페이지에서 가장 기본이 되는 필드 — 반드시 존재해야 함
        assertTrue(
            product.name is Field.Extracted || product.name is Field.Inferred,
            "상품명이 추출되지 않았습니다: ${product.name}",
        )
    }
}
