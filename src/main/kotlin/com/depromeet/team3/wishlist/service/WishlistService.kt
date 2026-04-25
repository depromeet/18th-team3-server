package com.depromeet.team3.wishlist.service

import com.depromeet.team3.product.domain.Product
import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.product.service.ProductExtractor
import com.depromeet.team3.wishlist.domain.Wish
import com.depromeet.team3.wishlist.repository.WishRepository
import com.depromeet.team3.wishlist.service.dto.WishRegisterResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class WishlistService(
    private val productExtractor: ProductExtractor,
    private val wishRepository: WishRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun register(rawUrl: String, guestId: UUID): WishRegisterResult {
        val link = ProductLink.parse(rawUrl)

        // dedup 검사를 추출 전에 먼저 — 중복이면 LLM 호출 비용 자체를 회피.
        if (wishRepository.existsByGuestIdAndProductLink(guestId, link)) {
            throw WishAlreadyExistsException(guestId = guestId, link = link)
        }

        val product = extractWithLatencyLog(link)
        val wish = wishRepository.save(Wish(guestId = guestId, product = product))
        return WishRegisterResult(wish = wish)
    }

    private fun extractWithLatencyLog(link: ProductLink): Product {
        val started = System.nanoTime()
        val product = productExtractor.extract(link)
        val elapsedMs = (System.nanoTime() - started) / 1_000_000
        log.info("extract latency: total={}ms url={}", elapsedMs, link)
        return product
    }
}
