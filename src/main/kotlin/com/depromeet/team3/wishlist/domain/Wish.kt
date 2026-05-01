package com.depromeet.team3.wishlist.domain

import com.depromeet.team3.common.domain.BaseEntity
import com.depromeet.team3.product.domain.Product
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "wishes")
class Wish(
    @Column(name = "guest_id", nullable = false, columnDefinition = "BINARY(16)")
    var guestId: UUID,

    @Embedded
    var product: Product,
) : BaseEntity<Long>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private var id: Long? = null

    // 미영속 Wish 는 엔티티가 아니다. id 가 채워지기 전에는 동등성 검사·해시 사용을
    // 거부해 미영속 인스턴스를 HashSet/HashMap 에 담거나 비교하는 잘못된 사용을 즉시 드러낸다.
    // (이전엔 0L 로 fallback 했으나 그러면 미영속 인스턴스끼리 모두 동등하게 처리되어
    //  컬렉션 동등성·중복 제거가 사일런트로 깨질 위험이 있었다.)
    override fun getId(): Long = id ?: error("Wish 는 영속화 후에만 id 를 가진다. 미영속 인스턴스에 동등성 검사·해시를 사용하지 않는다.")
}
