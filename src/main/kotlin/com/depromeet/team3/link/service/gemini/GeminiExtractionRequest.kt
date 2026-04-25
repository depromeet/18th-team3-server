package com.depromeet.team3.link.service.gemini

import com.fasterxml.jackson.annotation.JsonInclude
import java.net.URI

// Gemini JSON Schema 파서는 `"properties": null` 같은 잉여 null 필드를 스키마 위반으로 취급한다.
// 직렬화 단계에서 null 필드를 전부 생략해 요청 페이로드를 최소한의 형태로 유지한다.
@JsonInclude(JsonInclude.Include.NON_NULL)
data class GeminiExtractionRequest(
    val generationConfig: GenerationConfig,
    val contents: List<Content>,
    val tools: List<Tool>,
) {
    data class GenerationConfig(
        val responseMimeType: String,
        // Gemini 3 에서 tools 와 구조화 출력을 함께 쓰려면 구식 responseSchema 가 아닌 responseJsonSchema 를 사용.
        // 내부 스키마 형식은 OpenAPI-ish (소문자 type + nullable:true) 를 Gemini 가 받아준다.
        // JSON Schema 표준의 type 배열 표기(["string","null"]) 는 현 시점에 거부되므로 쓰지 않는다.
        val responseJsonSchema: JsonSchema,
    )

    data class Content(
        val parts: List<Part>,
    )

    data class Part(
        val text: String,
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class JsonSchema(
        val type: String,
        val description: String? = null,
        val properties: Map<String, JsonSchema>? = null,
        val items: JsonSchema? = null,
        val required: List<String>? = null,
        val nullable: Boolean? = null,
    )

    // url_context tool 활성화 시 Gemini 가 직접 URL 을 fetch 해 콘텐츠를 in-context 로 참조한다.
    // 옵션 필드가 비어 있어 Gemini 가 기본 동작으로 fetch 하도록 빈 객체 `{}` 로 직렬화한다.
    data class Tool(
        val urlContext: UrlContextOptions = UrlContextOptions,
    )

    data object UrlContextOptions

    companion object {
        private val SYSTEM_PROMPT = """
            You are a product information extractor. Given the URL of a product page,
            fetch the page (via the url_context tool) and extract information for the MAIN product.

            Ignore related products, recommended items, advertisements, and sidebar content.
            Focus on the primary product that the page is about.

            **Fields**:
            1. isProductPage (boolean, required): true if the page describes a single identifiable product for sale.
               false if this is a list/search/category page, an article, or non-commerce content.
            2. name (string): The product name exactly as displayed. null if isProductPage is false.
            3. regularPrice (integer): The original (pre-discount) price. Remove currency symbols, commas, decimals.
               If only a single price is shown (no discount), put it here.
            4. discountedPrice (integer): The final discounted price. null if no discount is shown.
            5. currency (string): ISO 4217 code (KRW, USD, JPY, EUR, etc.). Infer from page language/locale if ambiguous.
            6. imageUrl (string): ABSOLUTE URL of the primary product image. Prefer og:image meta tag,
               fallback to the main product image. Resolve relative URLs against the page URL.

            **Price rules**:
            - Single price, no discount indicator → regularPrice only, discountedPrice null.
            - Both original and sale prices visible → regularPrice = original, discountedPrice = sale.
            - Do NOT extract discount rate. The server computes it from regularPrice and discountedPrice.

            Respond with JSON only, matching the provided schema. Handle any language.
        """.trimIndent()

        private val EXTRACTION_SCHEMA = JsonSchema(
            type = "object",
            properties = mapOf(
                "isProductPage" to JsonSchema(type = "boolean"),
                "name" to JsonSchema(type = "string", nullable = true),
                "regularPrice" to JsonSchema(type = "integer", nullable = true),
                "discountedPrice" to JsonSchema(type = "integer", nullable = true),
                "currency" to JsonSchema(type = "string", nullable = true),
                "imageUrl" to JsonSchema(type = "string", nullable = true),
            ),
            required = listOf("isProductPage"),
        )

        private val URL_CONTEXT_TOOL = Tool()

        fun forUrlExtraction(url: URI): GeminiExtractionRequest =
            GeminiExtractionRequest(
                generationConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    responseJsonSchema = EXTRACTION_SCHEMA,
                ),
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = SYSTEM_PROMPT),
                            Part(text = "URL: $url"),
                        ),
                    ),
                ),
                tools = listOf(URL_CONTEXT_TOOL),
            )
    }
}
