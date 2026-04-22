package com.depromeet.team3.link.service.gemini

data class GeminiExtractionResponse(
    val candidates: List<Candidate>,
) {
    fun extractText(): String =
        candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw GeminiApiException.noTextPart()

    data class Candidate(
        val content: Content,
    )

    data class Content(
        val parts: List<Part>,
    )

    data class Part(
        val text: String,
    )
}
