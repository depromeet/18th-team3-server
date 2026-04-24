package com.depromeet.team3.link.service.gemini

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gemini")
data class GeminiProperties(
    val apiKey: String,
    val model: String = "gemini-3-flash-preview",
) {
    init {
        require(apiKey.isNotBlank()) {
            "GEMINI_API_KEY 가 설정되지 않았습니다. .env 또는 환경변수에 값을 지정하세요."
        }
        require(model.isNotBlank()) { "gemini.model 이 비어 있습니다." }
    }

    // data class 기본 toString 은 apiKey 를 그대로 노출하므로, 로그 유출 방지를 위해 마스킹한다.
    override fun toString(): String = "GeminiProperties(apiKey=*secret*, model=$model)"
}
