package com.depromeet.team3.guest.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.domain.Persistable

@Entity
class Guest(
    @Id
    @Column(name = "id")
    private val guestId: String,
) : Persistable<String> {
    override fun getId(): String = guestId
    override fun isNew(): Boolean = true
}
