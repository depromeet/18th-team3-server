package com.depromeet.team3.wishlist.service

import com.depromeet.team3.product.domain.Product
import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.product.repository.ProductJpaRepository
import com.depromeet.team3.product.service.ProductExtractor
import com.depromeet.team3.wishlist.domain.Wish
import com.depromeet.team3.wishlist.repository.WishJpaRepository
import com.depromeet.team3.wishlist.service.dto.WishRegisterResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class WishlistService(
    private val productExtractor: ProductExtractor,
    private val productJpaRepository: ProductJpaRepository,
    private val wishJpaRepository: WishJpaRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun register(rawUrl: String, guestId: UUID): WishRegisterResult {
        val link = ProductLink.parse(rawUrl)
        val extracted = extractWithLatencyLog(link)
        val product = productJpaRepository.findBySourceUrl(extracted.sourceUrl)
            ?: productJpaRepository.save(extracted)
        val productId = requireNotNull(product.id) { "Product id is null after save" }

        if (wishJpaRepository.existsByGuestIdAndProductId(guestId, productId)) {
            throw WishAlreadyExistsException(guestId = guestId, productId = productId)
        }

        val wish = wishJpaRepository.save(
            Wish(
                guestId = guestId,
                productId = productId,
                snapshotRegularPrice = product.regularPrice,
                snapshotDiscountedPrice = product.discountedPrice,
            )
        )
        return WishRegisterResult(wish = wish, product = product)
    }

    private fun extractWithLatencyLog(link: ProductLink): Product {
        val started = System.nanoTime()
        val product = productExtractor.extract(link)
        val elapsedMs = (System.nanoTime() - started) / 1_000_000
        log.info("extract latency: total={}ms url={}", elapsedMs, link)
        return product
    }
}
