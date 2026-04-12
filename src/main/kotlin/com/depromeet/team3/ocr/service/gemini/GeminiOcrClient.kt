package com.depromeet.team3.ocr.service.gemini

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.ocr.service.OcrClient
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.body
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import java.util.Base64

@Component
class GeminiOcrClient(
    private val objectMapper: ObjectMapper,
    private val geminiProperties: GeminiProperties,
) : OcrClient {
    private val restClient = RestClient
        .builder()
        .baseUrl("https://generativelanguage.googleapis.com")
        .requestFactory(
            SimpleClientHttpRequestFactory().apply {
                setConnectTimeout(CONNECT_TIMEOUT_MS)
                setReadTimeout(READ_TIMEOUT_MS)
            },
        )
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
            mimeType,
        )

        // TODO: Gemini API 가 간헐적으로 503(ServiceUnavailable) 을 반환함. 재시도 로직 필요.
        //       - 대상: 5xx 및 네트워크 타임아웃
        //       - 방식: 지수 백오프 + 최대 N회 (e.g. Resilience4j Retry 또는 RestClient interceptor)
        //       - 주의: 4xx (ex. 잘못된 API 키, 지원하지 않는 mimeType) 는 재시도 대상에서 제외
        val response = try {
            restClient
                .post()
                .uri {
                    it
                        .path("/v1beta/models/{model}:generateContent")
                        .build(geminiProperties.model)
                }
                .header(GEMINI_API_KEY_HEADER, geminiProperties.apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body<GeminiOcrResponse>()
        } catch (e: RestClientResponseException) {
            throw when {
                e.statusCode.is5xxServerError -> GeminiApiException.upstreamError(e.message, e)
                else -> GeminiApiException.clientError(e.message, e)
            }
        } catch (e: ResourceAccessException) {
            throw GeminiApiException.upstreamError(e.message, e)
        } catch (e: Exception) {
            throw GeminiApiException.upstreamError(e.message, e)
        }
        response ?: throw GeminiApiException.emptyResponse()

        val text = response.extractText()
        return try {
            val ocrResult = objectMapper.readValue<GeminiOcrResult>(text)
            ocrResult.toProduct()
        } catch (e: Exception) {
            throw GeminiApiException.parseError(e.message, e)
        }
    }

    companion object {
        private const val CONNECT_TIMEOUT_MS = 5_000
        private const val READ_TIMEOUT_MS = 30_000

        // API 키를 URL 쿼리파라미터 대신 헤더로 전달해 access log 등에 키가 남지 않도록 함.
        // https://ai.google.dev/gemini-api/docs/api-key#provide-api-key-explicitly
        private const val GEMINI_API_KEY_HEADER = "x-goog-api-key"

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
