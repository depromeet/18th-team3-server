package com.depromeet.team3.ocr.service.gemini

/**
 * Gemini API의 generateContent 응답을 매핑하는 클래스.
 *
 * Gemini API는 범용적으로 설계되어 있어 응답이 항상 아래와 같은 중첩 리스트 구조로 옴:
 * - candidates: 같은 요청에 대해 여러 답변 후보를 생성할 수 있음 (candidateCount 파라미터로 제어, 기본값 1)
 * - parts: 하나의 답변이 텍스트, 이미지 등 여러 파트로 구성될 수 있음
 *
 * 이 프로젝트에서는 후보 1개, 텍스트 파트 1개만 사용하므로 extractText()에서 firstOrNull()로 바로 꺼냄.
 */
data class GeminiOcrResponse(
    val candidates: List<Candidate>,
) {
    /**
     * 응답에서 첫 번째 텍스트 파트를 꺼낸다.
     *
     * `candidates` 또는 `parts` 가 빈 리스트인 경우 (safety filter 차단, 모델 실패 등)
     * `first()` 는 `NoSuchElementException` 을 던지는데, 호출 측에서 이 예외의 의미를
     * 추론하려면 구현을 들여다봐야 한다. DTO 단에서 명시적으로 GeminiApiException 으로
     * 변환해 "응답이 비어 있다" 는 의도를 그대로 드러낸다.
     */
    fun extractText(): String =
        candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw GeminiApiException("Gemini 응답에 텍스트 파트가 없습니다.")

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
