package com.depromeet.team3.guest.controller

import com.depromeet.team3.guest.controller.dto.GuestResponse
import com.depromeet.team3.guest.service.GuestService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/guests")
class GuestController(
    private val guestService: GuestService,
) {
    @PostMapping
    fun issueGuestUuid(): GuestResponse = GuestResponse(guestService.issueGuestId())
}
