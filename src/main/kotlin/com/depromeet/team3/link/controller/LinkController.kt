package com.depromeet.team3.link.controller

import com.depromeet.team3.link.controller.dto.LinkRegisterRequest
import com.depromeet.team3.link.controller.dto.LinkRegisterResponse
import com.depromeet.team3.link.service.LinkService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/links")
class LinkController(
    private val linkService: LinkService,
) {
    @PostMapping
    fun register(@RequestBody request: LinkRegisterRequest): LinkRegisterResponse {
        val extracted = linkService.register(request.url)
        return LinkRegisterResponse.from(extracted.product)
    }
}
