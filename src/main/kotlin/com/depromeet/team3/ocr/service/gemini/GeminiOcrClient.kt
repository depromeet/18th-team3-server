package com.depromeet.team3.ocr.service.gemini

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.ocr.service.OcrClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import java.util.*

@Component
class GeminiOcrClient(
    private val objectMapper: ObjectMapper,
    private val geminiProperties: GeminiProperties,
) : OcrClient {
    private val restClient = RestClient
        .builder()
        .baseUrl("https://generativelanguage.googleapis.com")
        .build()

    override fun analyzeImage(
        imageBytes: ByteArray,
        mimeType: String,
    ): Product {
        require(mimeType in SUPPORTED_MIME_TYPES) {
            "Gemini가 지원하지 않는 이미지 형식입니다: $mimeType (지원: ${SUPPORTED_MIME_TYPES.joinToString()})"
        }

        val base64Image = Base64
            .getEncoder()
            .encodeToString(imageBytes)

        val request = GeminiOcrRequest.forImageAnalysis(
            base64Image,
            mimeType
        )

        // TODO: Gemini API 가 간헐적으로 503(ServiceUnavailable) 을 반환함. 재시도 로직 필요.
        //       - 대상: 5xx 및 네트워크 타임아웃
        //       - 방식: 지수 백오프 + 최대 N회 (e.g. Resilience4j Retry 또는 RestClient interceptor)
        //       - 주의: 4xx (ex. 잘못된 API 키, 지원하지 않는 mimeType) 는 재시도 대상에서 제외
        val response = restClient
            .post()
            .uri {
                it
                    .path("/v1beta/models/{model}:generateContent")
                    .queryParam(
                        "key",
                        geminiProperties.apiKey
                    )
                    .build(geminiProperties.model)
            }
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body<GeminiOcrResponse>()
            ?: throw GeminiApiException("Gemini 응답이 비어 있습니다.")

        val ocrResult = objectMapper.readValue<GeminiOcrResult>(response.extractText())
        return ocrResult.toProduct()
    }

    companion object {
        // https://ai.google.dev/gemini-api/docs/vision
        private val SUPPORTED_MIME_TYPES = setOf(
            "image/png",
            "image/jpeg",
            "image/webp",
            "image/heic",
            "image/heif",
        )
    }
}
