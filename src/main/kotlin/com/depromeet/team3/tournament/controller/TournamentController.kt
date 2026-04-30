package com.depromeet.team3.tournament.controller

import com.depromeet.team3.common.response.ApiResponse
import com.depromeet.team3.tournament.controller.dto.StartTournamentRequest
import com.depromeet.team3.tournament.controller.dto.StartTournamentResponse
import com.depromeet.team3.tournament.service.TournamentService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/tournaments")
class TournamentController(
    private val tournamentService: TournamentService,
) {
    @PostMapping
    fun start(
        @RequestHeader("X-User-Id") userId: UUID,
        @RequestBody @Valid request: StartTournamentRequest,
    ): ApiResponse<StartTournamentResponse> {
        val tournamentId = tournamentService.start(userId, request.toStartTournament())
        return ApiResponse.created(StartTournamentResponse(tournamentId))
    }
}
