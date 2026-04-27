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

    // BaseEntity#equals/hashCode 가 getId() 를 호출한다. 영속화 전 인스턴스를
    // HashSet/HashMap 에 담거나 Hibernate 가 동등성 검사를 할 때 throw 하면
    // 의도치 않게 IllegalArgumentException 이 새어나가므로 0 으로 떨어뜨린다.
    override fun getId(): Long = id ?: 0L
}
