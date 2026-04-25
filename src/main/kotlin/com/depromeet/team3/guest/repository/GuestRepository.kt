package com.depromeet.team3.guest.repository

import com.depromeet.team3.guest.domain.Guest
import java.util.UUID

interface GuestRepository {
    fun save(id: UUID): Guest
}
