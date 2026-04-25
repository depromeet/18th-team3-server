package com.depromeet.team3.guest.service

import com.depromeet.team3.guest.domain.Guest
import com.depromeet.team3.guest.repository.GuestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GuestService(
    private val guestRepository: GuestRepository,
) {
    @Transactional
    fun issueGuestUuid(): String {
        var uuid: String
        do {
            uuid = UUID.randomUUID().toString()
        } while (guestRepository.existsByUuid(uuid))

        guestRepository.save(Guest(uuid))
        return uuid
    }
}
