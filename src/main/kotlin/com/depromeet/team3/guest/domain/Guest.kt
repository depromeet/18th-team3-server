package com.depromeet.team3.guest.domain

import com.depromeet.team3.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "guests")
class Guest(
    @Id
    @Column(name = "id")
    private val id: UUID = UUID.randomUUID(),
) : BaseEntity<UUID>() {
    // Guest 는 client-side id 라 생성 시점에 항상 채워진다. 그래도 BaseEntity 의 nullable
    // 시그니처에 맞춰 getIdOrNull 만 override 하고 getId 는 부모의 default 를 사용한다.
    override fun getIdOrNull(): UUID = id
}
