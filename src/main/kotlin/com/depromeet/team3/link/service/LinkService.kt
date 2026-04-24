package com.depromeet.team3.link.service

import com.depromeet.team3.common.domain.Product
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URI

@Service
class LinkService(
    private val productExtractor: ProductExtractor,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun register(url: String): Product {
        val cleaned = validateUrl(url)

        val started = System.nanoTime()
        val product = productExtractor.extract(cleaned)
        val elapsedMs = (System.nanoTime() - started) / 1_000_000

        // sync/async 전환 결정을 위한 latency 측정 로그. url_context 는 fetch+추론이 합쳐져 있어 분리 측정 불가.
        log.info("link register latency: total={}ms url={}", elapsedMs, cleaned)

        return product
    }

    private fun validateUrl(raw: String): String {
        val trimmed = raw.trim()
        require(trimmed.isNotBlank()) { "URL이 비어 있습니다." }
        require(trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            "http/https URL만 허용합니다."
        }
        try {
            URI.create(trimmed)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("유효한 URL 형식이 아닙니다: $trimmed", e)
        }
        return trimmed
    }
}
