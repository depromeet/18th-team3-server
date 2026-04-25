package com.depromeet.team3.guest.repository

import com.depromeet.team3.guest.domain.Guest
import org.springframework.stereotype.Repository

@Repository
internal class GuestRepositoryImpl(
    private val guestJpaRepository: GuestJpaRepository,
) : GuestRepository {
    override fun existsByUuid(uuid: String): Boolean = guestJpaRepository.existsById(uuid)

    override fun save(guest: Guest): Guest = guestJpaRepository.save(guest)
}
