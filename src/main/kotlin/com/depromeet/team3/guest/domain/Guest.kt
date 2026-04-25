package com.depromeet.team3.guest.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Guest(
    @Id
    val id: String,
)
