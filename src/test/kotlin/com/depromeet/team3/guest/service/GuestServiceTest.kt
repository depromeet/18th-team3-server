package com.depromeet.team3.guest.service

import com.depromeet.team3.guest.domain.Guest
import com.depromeet.team3.guest.repository.GuestRepository
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GuestServiceTest {

    private class StubGuestRepository : GuestRepository {
        val saved = mutableListOf<Guest>()
        var existsResults: ArrayDeque<Boolean> = ArrayDeque()
        val existsCalledFor = mutableListOf<String>()

        override fun existsByUuid(uuid: String): Boolean {
            existsCalledFor.add(uuid)
            return existsResults.removeFirstOrNull() ?: false
        }

        override fun save(guest: Guest): Guest {
            saved.add(guest)
            return guest
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

        assertEquals(1, repository.saved.size)
        assertEquals(uuid, repository.saved[0].uuid)
    }

    @Test
    fun `issueGuestUuid 를 여러 번 호출하면 매번 새로운 UUID 가 발급된다`() {
        val first = guestService.issueGuestUuid()
        val second = guestService.issueGuestUuid()
        val third = guestService.issueGuestUuid()

        val uuids = setOf(first, second, third)
        assertEquals(3, uuids.size)
        assertEquals(3, repository.saved.size)
    }

    @Test
    fun `이미 존재하는 UUID 면 새 UUID 를 다시 발급한다`() {
        repository.existsResults = ArrayDeque(listOf(true, false))

        val issued = guestService.issueGuestUuid()

        UUID.fromString(issued)
        assertEquals(2, repository.existsCalledFor.size)
        assertEquals(1, repository.saved.size)
        assertEquals(issued, repository.saved[0].uuid)
    }

    @Test
    fun `existsByUuid 가 false 일 때만 save 가 호출된다`() {
        val uuid = guestService.issueGuestUuid()
        val unrelated = UUID.randomUUID().toString()
        assertNotEquals(uuid, unrelated)

        assertEquals(listOf(uuid), repository.saved.map { it.uuid })
    }
}
