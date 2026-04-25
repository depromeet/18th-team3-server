package com.depromeet.team3.guest.domain

import com.depromeet.team3.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
class Guest(
    @Id
    @Column(name = "id")
    private val id: UUID = UUID.randomUUID(),
) : BaseEntity<UUID>() {
    override fun getId(): UUID {
        return id
    }
}
