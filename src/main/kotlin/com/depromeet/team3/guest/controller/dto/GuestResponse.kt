package com.depromeet.team3.guest.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "게스트 ID 발급 응답")
data class GuestResponse(
    @field:Schema(
        description = "발급된 게스트 식별자 (UUID v4)",
        example = "8f1a3c2b-9d44-4e2a-9b12-1a2b3c4d5e6f",
        format = "uuid",
    )
    val guestId: UUID,
)
