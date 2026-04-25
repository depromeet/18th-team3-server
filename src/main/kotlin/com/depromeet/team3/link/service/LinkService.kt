package com.depromeet.team3.link.service

import com.depromeet.team3.link.domain.ProductLink
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LinkService(
    private val productExtractor: ProductExtractor,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun register(rawUrl: String): ExtractedLink {
        val link = ProductLink.parse(rawUrl)
        val started = System.nanoTime()
        val product = productExtractor.extract(link)
        recordLatency(started, link)
        return ExtractedLink(link = link, product = product)
    }

    // sync/async 전환 결정을 위한 latency 측정 로그. url_context 는 fetch+추론이 합쳐져 있어 분리 측정 불가.
    private fun recordLatency(startedNanos: Long, link: ProductLink) {
        val elapsedMs = (System.nanoTime() - startedNanos) / 1_000_000
        log.info("link register latency: total={}ms url={}", elapsedMs, link)
    }
}
