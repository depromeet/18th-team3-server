package com.depromeet.team3.wishlist.service

import com.depromeet.team3.link.domain.ProductLink
import com.depromeet.team3.link.service.LinkService
import com.depromeet.team3.product.repository.ProductEntity
import com.depromeet.team3.product.repository.ProductJpaRepository
import com.depromeet.team3.wishlist.repository.WishlistEntity
import com.depromeet.team3.wishlist.repository.WishlistJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WishlistService(
    private val linkService: LinkService,
    private val productJpaRepository: ProductJpaRepository,
    private val wishlistJpaRepository: WishlistJpaRepository,
) {
    @Transactional
    fun register(rawUrl: String, userId: Long): WishlistEntity {
        val product = linkService.register(rawUrl)
        // ProductLink.parse 는 멱등이라 LinkService 내부 정규화와 동일한 결과를 보장한다.
        val sourceUrl = ProductLink.parse(rawUrl).toString()

        val productEntity = productJpaRepository.findBySourceUrl(sourceUrl)
            ?: productJpaRepository.save(
                ProductEntity(
                    sourceUrl = sourceUrl,
                    name = product.name,
                    regularPrice = product.regularPrice,
                    discountedPrice = product.discountedPrice,
                    currency = product.currency,
                    imageUrl = product.imageUrl,
                )
            )

        val productId = requireNotNull(productEntity.id) { "ProductEntity id is null after save" }

        if (wishlistJpaRepository.existsByUserIdAndProductId(userId, productId)) {
            throw WishlistAlreadyExistsException(userId = userId, productId = productId)
        }

        return wishlistJpaRepository.save(
            WishlistEntity(
                userId = userId,
                productId = productId,
                snapshotRegularPrice = product.regularPrice,
                snapshotDiscountedPrice = product.discountedPrice,
            )
        )
    }
}
