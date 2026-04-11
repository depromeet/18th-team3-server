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

    // data class 의 기본 toString() 은 모든 프로퍼티를 그대로 노출한다.
    // 바인딩 실패 로그, 디버그 출력, 예외 메시지 등에 객체가 딸려 나가더라도
    // apiKey 가 유출되지 않도록 마스킹한다.
    override fun toString(): String = "GeminiProperties(apiKey=*secret*, model=$model)"
}
