package com.depromeet.team3.guest.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.domain.Persistable
import java.util.UUID

@Entity
class Guest(
    @Id
    @Column(name = "id")
    private val guestId: UUID,
) : Persistable<String> {
    override fun getId(): String = guestId.toString()
    override fun isNew(): Boolean = true
}
