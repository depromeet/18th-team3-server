package com.depromeet.team3.guest.repository

import com.depromeet.team3.guest.domain.Guest
import org.springframework.stereotype.Repository

@Repository
class GuestRepositoryImpl(
    private val guestJpaRepository: GuestJpaRepository,
) : GuestRepository {
    override fun existsByUuid(uuid: String): Boolean = guestJpaRepository.existsById(uuid)

    override fun save(id: String): Guest = guestJpaRepository.save(Guest(id))
}
