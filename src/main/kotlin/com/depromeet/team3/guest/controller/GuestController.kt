package com.depromeet.team3.guest.controller

import com.depromeet.team3.common.response.ApiResponseBody
import com.depromeet.team3.guest.controller.dto.GuestResponse
import com.depromeet.team3.guest.service.GuestService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Guest", description = "게스트 식별자 발급 API")
@RestController
@RequestMapping("/api/v1/guests")
class GuestController(
    private val guestService: GuestService,
) {
    @Operation(
        summary = "게스트 ID 발급",
        description = """
            로그인 없이 위시리스트를 사용하는 게스트에게 영속 식별자(UUID v4)를 발급한다.
            클라이언트는 발급받은 값을 안전하게 보관하고, 이후 위시리스트 API 호출 시 함께 전달한다.
        """,
    )
    @ApiResponse(
        responseCode = "200",
        description = "게스트 ID 발급 성공",
        content = [
            Content(
                schema = Schema(implementation = GuestResponse::class),
                examples = [
                    ExampleObject(
                        name = "발급 성공",
                        value = """
                            {
                              "status": 200,
                              "data": { "guestId": "8f1a3c2b-9d44-4e2a-9b12-1a2b3c4d5e6f" },
                              "detail": "정상적으로 처리되었습니다.",
                              "code": "OK"
                            }
                        """,
                    ),
                ],
            ),
        ],
    )
    @PostMapping
    fun issueGuestId(): ApiResponseBody<GuestResponse> = ApiResponseBody.ok(GuestResponse(guestService.issueGuestId()))
}
