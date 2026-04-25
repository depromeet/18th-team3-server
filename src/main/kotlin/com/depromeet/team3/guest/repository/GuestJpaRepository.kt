package com.depromeet.team3.guest.repository

import com.depromeet.team3.guest.domain.Guest
import org.springframework.data.jpa.repository.JpaRepository

internal interface GuestJpaRepository : JpaRepository<Guest, String>
