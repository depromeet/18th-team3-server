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

    fun register(rawUrl: String): Product {
        val url = parse(rawUrl)

        val started = System.nanoTime()
        val product = productExtractor.extract(url)
        val elapsedMs = (System.nanoTime() - started) / 1_000_000

        // sync/async 전환 결정을 위한 latency 측정 로그. url_context 는 fetch+추론이 합쳐져 있어 분리 측정 불가.
        log.info("link register latency: total={}ms url={}", elapsedMs, url)

        return product
    }

    private fun parse(raw: String): URI {
        val trimmed = raw.trim()
        require(trimmed.isNotBlank()) { "URL이 비어 있습니다." }
        val uri = try {
            URI.create(trimmed)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("유효한 URL 형식이 아닙니다: $trimmed", e)
        }
        // URI.create 는 스킴 없는 "example.com/product" 도 relative URI 로 통과시키므로 명시 검증.
        require(uri.scheme in HTTP_SCHEMES) { "http/https URL만 허용합니다." }
        return uri
    }

    companion object {
        private val HTTP_SCHEMES = setOf("http", "https")
    }
}
