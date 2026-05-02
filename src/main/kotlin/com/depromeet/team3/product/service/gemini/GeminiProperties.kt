package com.depromeet.team3.product.service.gemini

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gemini")
data class GeminiProperties(
    val apiKey: String,
    val model: String = "gemini-2.5-flash",
) {
    init {
        require(apiKey.isNotBlank()) { "GEMINI_API_KEY 가 비어 있습니다." }
        require(model.isNotBlank()) { "gemini.model 이 비어 있습니다." }
    }

    // data class 기본 toString 은 apiKey 를 그대로 노출하므로, 로그 유출 방지를 위해 마스킹한다.
    override fun toString(): String = "GeminiProperties(apiKey=*secret*, model=$model)"
}
