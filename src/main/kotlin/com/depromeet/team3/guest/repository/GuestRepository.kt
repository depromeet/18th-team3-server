package com.depromeet.team3.guest.repository

import com.depromeet.team3.guest.domain.Guest

interface GuestRepository {
    fun save(id: String): Guest
}
