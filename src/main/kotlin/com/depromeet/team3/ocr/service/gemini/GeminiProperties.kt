package com.depromeet.team3.ocr.service.gemini

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gemini")
data class GeminiProperties(
    val apiKey: String,
    val model: String = "gemini-2.5-flash",
) {
    init {
        require(apiKey.isNotBlank()) {
            "GEMINI_API_KEY 가 설정되지 않았습니다. .env 또는 환경변수에 값을 지정하세요."
        }
        require(model.isNotBlank()) { "gemini.model 이 비어 있습니다." }
    }
}
