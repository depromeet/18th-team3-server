package com.depromeet.team3.wishlist.service

import com.depromeet.team3.product.domain.Product
import com.depromeet.team3.wishlist.domain.Wish
import com.depromeet.team3.wishlist.repository.WishRepository
import com.depromeet.team3.wishlist.service.dto.WishRegisterResult
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

// WishlistService 의 register 가 외부 LLM 호출을 트랜잭션 바깥에 두도록
// 영속화만 별도 빈으로 분리. 같은 빈에서 호출하면 Spring AOP proxy 를
// 거치지 않아 @Transactional 가 무력화되기 때문이다.
@Service
class WishPersistenceService(
    private val wishRepository: WishRepository,
) {
    @Transactional
    fun persist(guestId: UUID, product: Product): WishRegisterResult =
        try {
            val wish = wishRepository.save(Wish(guestId = guestId, product = product))
            WishRegisterResult(wish = wish)
        } catch (e: DataIntegrityViolationException) {
            // 동시 요청이 register 단계의 dedup 체크를 모두 통과한 뒤
            // uk_wishes_guest_source 제약에 걸린 race 케이스. 500 대신 409 로 응답한다.
            throw WishAlreadyExistsException(guestId = guestId, link = product.link)
        }
}
