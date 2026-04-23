package com.depromeet.team3.link.service.gemini

data class GeminiExtractionRequest(
    val generationConfig: GenerationConfig,
    val contents: List<Content>,
) {
    data class GenerationConfig(
        val responseMimeType: String,
        val responseSchema: Schema,
    )

    data class Content(
        val parts: List<Part>,
    )

    data class Part(
        val text: String,
    )

    data class Schema(
        val type: SchemaType,
        val description: String? = null,
        val properties: Map<String, Schema>? = null,
        val items: Schema? = null,
        val required: List<String>? = null,
        val nullable: Boolean? = null,
    )

    enum class SchemaType {
        OBJECT,
        ARRAY,
        STRING,
        INTEGER,
        BOOLEAN,
    }

    companion object {
        private val SYSTEM_PROMPT = """
            You are a product information extractor. Given the HTML of a product page and its URL,
            extract information for the MAIN product of the page.

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

        private val EXTRACTION_SCHEMA = Schema(
            type = SchemaType.OBJECT,
            properties = mapOf(
                "isProductPage" to Schema(type = SchemaType.BOOLEAN),
                "name" to Schema(type = SchemaType.STRING, nullable = true),
                "regularPrice" to Schema(type = SchemaType.INTEGER, nullable = true),
                "discountedPrice" to Schema(type = SchemaType.INTEGER, nullable = true),
                "currency" to Schema(type = SchemaType.STRING, nullable = true),
                "imageUrl" to Schema(type = SchemaType.STRING, nullable = true),
            ),
            required = listOf("isProductPage"),
        )

        fun forHtmlExtraction(url: String, html: String): GeminiExtractionRequest =
            GeminiExtractionRequest(
                generationConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    responseSchema = EXTRACTION_SCHEMA,
                ),
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = SYSTEM_PROMPT),
                            Part(text = "URL: $url\n\nHTML:\n$html"),
                        ),
                    ),
                ),
            )
    }
}
