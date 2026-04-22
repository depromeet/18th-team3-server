package com.depromeet.team3.link.service

import com.depromeet.team3.common.domain.Product
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URI

@Service
class LinkService(
    private val pageFetcher: PageFetcher,
    private val productExtractor: ProductExtractor,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun register(url: String): Product {
        val cleaned = validateUrl(url)

        val started = System.nanoTime()
        val page = pageFetcher.fetch(cleaned)
        val fetchedAt = System.nanoTime()

        val product = productExtractor.extract(page)
        val extractedAt = System.nanoTime()

        // sync/async 전환 결정을 위한 단계별 latency 측정 로그.
        log.info(
            "link register latency: fetch={}ms extract={}ms total={}ms url={}",
            (fetchedAt - started) / 1_000_000,
            (extractedAt - fetchedAt) / 1_000_000,
            (extractedAt - started) / 1_000_000,
            cleaned,
        )

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
