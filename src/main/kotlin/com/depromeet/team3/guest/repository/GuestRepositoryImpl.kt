package com.depromeet.team3.guest.repository

import com.depromeet.team3.guest.domain.Guest
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class GuestRepositoryImpl(
    private val guestJpaRepository: GuestJpaRepository,
) : GuestRepository {
    override fun save(id: UUID): Guest = guestJpaRepository.save(Guest(id))
}
