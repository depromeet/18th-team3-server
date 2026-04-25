package com.depromeet.team3.guest.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "guests")
class Guest(
    @Id
    val uuid: String,
)
