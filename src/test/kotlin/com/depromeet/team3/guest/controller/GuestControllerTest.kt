package com.depromeet.team3.guest.controller

import com.depromeet.team3.guest.service.GuestService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class GuestControllerTest {

    @Mock
    private lateinit var guestService: GuestService

    @InjectMocks
    private lateinit var guestController: GuestController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(guestController).build()
    }

    @Test
    fun `POST api v1 guests 는 발급된 UUID 를 guestId 필드로 반환한다`() {
        val expectedUuid = UUID.fromString("11111111-2222-3333-4444-555555555555")
        given(guestService.issueGuestId()).willReturn(expectedUuid)

        mockMvc.perform(post("/api/v1/guests"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.guestId").value(expectedUuid.toString()))
    }

    @Test
    fun `POST api v1 guests 는 매 요청마다 GuestService 를 호출해 새 UUID 를 받아온다`() {
        val first = UUID.randomUUID()
        val second = UUID.randomUUID()
        given(guestService.issueGuestId()).willReturn(first, second)

        mockMvc.perform(post("/api/v1/guests"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.guestId").value(first.toString()))

        mockMvc.perform(post("/api/v1/guests"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.guestId").value(second.toString()))
    }
}
