package com.depromeet.team3.ocr.service.gemini

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.common.domain.Product.Field
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

/**
 * Gemini OCR **수동 실험용** 테스트.
 *
 * 목적: 모델/이미지 포맷 등 설정을 바꿔가며 지연시간·추출 품질을 관찰/비교한다.
 * assertion 보다는 콘솔 출력을 보고 눈으로 판단하는 용도.
 *
 * ---
 *
 * ## 왜 별도 클래스로 분리했는가
 *
 * 1. **느림** — 조합마다 수 초~수십 초의 실제 API 호출. CI/일반 `./gradlew test` 에
 *    포함되면 빌드가 불필요하게 느려지고 flaky 해진다.
 * 2. **Flaky** — free tier 는 RPM 10, 연속 호출 시 503(UNAVAILABLE) 이 흔하게 터진다.
 *    정상 빌드를 깨뜨리면 안 됨.
 * 3. **실험 목적** — "이 모델이 저 모델보다 나은가" 같은 의사결정을 위한 관찰 용도지
 *    회귀 방지용 테스트가 아니다.
 *
 * ## 실행 방법
 *
 * - **IDE (권장)**: 클래스/메서드 옆 ▶️ 클릭. IntelliJ 가 `@EnabledIf*` 조건을
 *   자동 비활성화하므로 환경변수 설정 없이도 바로 실행됨. `@CsvSource` 의 특정
 *   한 줄만 남기고 실행하면 원하는 조합만 관찰 가능.
 * - **CLI**: `GEMINI_EXPERIMENT=1 GEMINI_API_KEY=... ./gradlew test --tests "*ExperimentTest*"`
 * - **일반 `./gradlew test`**: `GEMINI_EXPERIMENT` 없으면 **전체 skip**.
 *
 * ## Rate limit 주의사항
 *
 * - `gemini-2.5-flash` : free tier 10 RPM
 * - preview 모델      : 더 낮음, 시간대에 따라 거의 사용 불가
 * - 4개 조합 연속 실행 시 필연적으로 503 이 발생 → `Thread.sleep` 으로 간격 확보
 * - 그래도 실패하면 수십 초~1분 기다렸다가 재실행
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_EXPERIMENT", matches = ".+")
class GeminiOcrExperimentTest(
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val geminiProperties: GeminiProperties,
) {

    @ParameterizedTest(name = "모델={0}, 포맷={1}")
    @CsvSource(
        "gemini-2.5-flash,               png",
        "gemini-2.5-flash,               jpeg",
        "gemini-3.1-flash-lite-preview,  png",
        "gemini-3.1-flash-lite-preview,  jpeg",
    )
    fun `모델과 이미지 포맷 조합별 추출 결과를 비교한다`(
        model: String,
        format: String,
    ) {
        // RPM 한도 안에 안착하도록 invocation 간 간격 확보.
        Thread.sleep(INVOCATION_INTERVAL_MS)

        val (imageBytes, mimeType) = loadTestImage(format)
        // 프로덕션과 동일한 경로(application.yml + .env.test) 로 주입된 apiKey 를 재사용하고,
        // 모델 ID 만 갈아끼운다.
        val client = GeminiOcrClient(
            objectMapper = objectMapper,
            geminiProperties = geminiProperties.copy(model = model),
        )

        lateinit var product: Product
        val elapsedMs = measureTimeMillis {
            product = client.analyzeImage(imageBytes, mimeType)
        }

        val sizeKb = imageBytes.size / 1024
        println("=== [$model | $format | ${sizeKb}KB] elapsed=${elapsedMs}ms ===")
        println("  name     = ${product.name}")
        println("  price    = ${product.price}")
        println("  category = ${product.category}")

        assertTrue(
            product.name is Field.Extracted || product.name is Field.Inferred,
            "[$model | $format] 상품명이 추출되지 않았습니다: ${product.name}",
        )
    }

    private fun loadTestImage(format: String): Pair<ByteArray, String> {
        val (resource, mimeType) = when (format) {
            "jpeg" -> "/test-product.jpg" to "image/jpeg"
            else -> "/test-product.png" to "image/png"
        }
        val bytes = javaClass
            .getResourceAsStream(resource)
            ?.readAllBytes()
            ?: throw IllegalStateException("src/test/resources$resource 파일을 준비해주세요.")
        return bytes to mimeType
    }

    companion object {
        private const val INVOCATION_INTERVAL_MS = 8_000L
    }
}
