package com.depromeet.team3.guest.service

import com.depromeet.team3.guest.domain.Guest
import com.depromeet.team3.guest.repository.GuestRepository
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class GuestServiceTest {

    private class StubGuestRepository : GuestRepository {
        val saved = mutableListOf<String>()

        override fun save(id: String): Guest {
            saved.add(id)
            return Guest(id)
        }
    }

    private val repository = StubGuestRepository()
    private val guestService = GuestService(repository)

    @Test
    fun `issueGuestUuid 는 UUID 형식의 문자열을 반환한다`() {
        val uuid = guestService.issueGuestUuid()

        UUID.fromString(uuid)
    }

    @Test
    fun `issueGuestUuid 는 발급한 UUID 를 저장소에 저장한다`() {
        val uuid = guestService.issueGuestUuid()

        assertEquals(listOf(uuid), repository.saved)
    }

    @Test
    fun `issueGuestUuid 를 여러 번 호출하면 매번 새로운 UUID 가 발급된다`() {
        val first = guestService.issueGuestUuid()
        val second = guestService.issueGuestUuid()
        val third = guestService.issueGuestUuid()

        val uuids = setOf(first, second, third)
        assertEquals(3, uuids.size)
        assertEquals(listOf(first, second, third), repository.saved)
    }
}
