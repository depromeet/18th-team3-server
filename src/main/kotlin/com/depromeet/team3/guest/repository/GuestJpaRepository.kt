package com.depromeet.team3.guest.repository

import com.depromeet.team3.guest.domain.Guest
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GuestJpaRepository : JpaRepository<Guest, UUID>
