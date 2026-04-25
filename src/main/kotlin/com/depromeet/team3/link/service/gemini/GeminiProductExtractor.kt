package com.depromeet.team3.link.service.gemini

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.link.service.ProductExtractor
import org.slf4j.LoggerFactory
import java.net.URI
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.body
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

@Component
class GeminiProductExtractor(
    private val objectMapper: ObjectMapper,
    private val geminiProperties: GeminiProperties,
) : ProductExtractor {
    private val log = LoggerFactory.getLogger(javaClass)

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

    override fun extract(url: URI): Product {
        val request = GeminiExtractionRequest.forUrlExtraction(url)

        // TODO: Gemini API 의 간헐적 5xx/타임아웃 대응 재시도 로직 필요. RETRYABLE 카테고리 예외 대상.
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
                .body<GeminiExtractionResponse>()
        } catch (e: RestClientResponseException) {
            throw when {
                e.statusCode.is5xxServerError -> GeminiApiException.upstreamError(e)
                else -> GeminiApiException.clientError(e)
            }
        } catch (e: ResourceAccessException) {
            throw GeminiApiException.upstreamError(e)
        } ?: throw GeminiApiException.emptyResponse()

        // url_context fetch 결과는 블랙박스라 추적성을 위해 남긴다. 차단/캐시/실패 패턴 수집용.
        response.urlContextMetadata()?.urlMetadata?.forEach { meta ->
            log.info(
                "url_context fetch: status={} url={}",
                meta.urlRetrievalStatus,
                meta.retrievedUrl,
            )
        }

        val text = response.extractText()
        val result = try {
            objectMapper.readValue<GeminiExtractionResult>(text)
        } catch (e: Exception) {
            throw GeminiApiException.parseError(e)
        }
        return result.toProduct()
    }

    companion object {
        private const val CONNECT_TIMEOUT_MS = 5_000

        // url_context fetch + 모델 추론이 합쳐지므로 넉넉하게.
        private const val READ_TIMEOUT_MS = 60_000

        // API 키는 access log 에 남지 않도록 쿼리 대신 헤더로 전달.
        // https://ai.google.dev/gemini-api/docs/api-key#provide-api-key-explicitly
        private const val GEMINI_API_KEY_HEADER = "x-goog-api-key"
    }
}
