package com.depromeet.team3.tournament.controller

import com.depromeet.team3.common.response.ApiResponseBody
import com.depromeet.team3.tournament.controller.dto.RecordMatchRequest
import com.depromeet.team3.tournament.controller.dto.StartTournamentRequest
import com.depromeet.team3.tournament.controller.dto.StartTournamentResponse
import com.depromeet.team3.tournament.controller.dto.TournamentInfoResponse
import com.depromeet.team3.tournament.service.TournamentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "Tournament", description = "토너먼트 API")
@RestController
@RequestMapping("/api/v1/tournaments")
class TournamentController(
    private val tournamentService: TournamentService,
) {
    @Operation(
        summary = "토너먼트 시작",
        description = "위시 아이템 목록으로 토너먼트를 생성하고 시작한다.",
    )
    @SwaggerApiResponse(
        responseCode = "201",
        description = "토너먼트 생성 성공",
        content = [
            Content(
                schema = Schema(implementation = StartTournamentResponse::class),
                examples = [
                    ExampleObject(
                        name = "생성 성공",
                        value = """
                            {
                              "status": 201,
                              "data": { "tournamentId": 1 },
                              "detail": "요청이 정상적으로 처리되었습니다.",
                              "code": "CREATED"
                            }
                        """,
                    ),
                ],
            ),
        ],
    )
    @PostMapping
    fun start(
        @RequestHeader("X-User-Id") userId: UUID,
        @RequestBody @Valid request: StartTournamentRequest,
    ): ApiResponseBody<StartTournamentResponse> {
        val tournamentId = tournamentService.start(userId, request.toStartTournament())
        return ApiResponseBody.created(StartTournamentResponse(tournamentId))
    }

    @Operation(
        summary = "매치 결과 기록",
        description = "토너먼트의 한 라운드 매치 결과(승자)를 기록한다.",
    )
    @SwaggerApiResponse(
        responseCode = "204",
        description = "매치 결과 기록 성공",
    )
    @PostMapping("/{tournamentId}/matches")
    fun recordMatch(
        @RequestHeader("X-User-Id") userId: UUID,
        @PathVariable tournamentId: Long,
        @RequestBody @Valid request: RecordMatchRequest,
    ): ApiResponseBody<Unit> {
        tournamentService.recordMatch(userId, request.toRecordMatch(tournamentId))
        return ApiResponseBody.noContent()
    }

    @Operation(
        summary = "토너먼트 조회",
        description = "토너먼트 ID로 토너먼트 정보와 매치 기록을 조회한다.",
    )
    @SwaggerApiResponse(
        responseCode = "200",
        description = "토너먼트 조회 성공",
        content = [
            Content(
                schema = Schema(implementation = TournamentInfoResponse::class),
                examples = [
                    ExampleObject(
                        name = "조회 성공",
                        value = """
                            {
                              "status": 200,
                              "data": {
                                "tournamentId": 1,
                                "finalWinnerWishItemId": 3,
                                "history": [
                                  {
                                    "currentRound": 1,
                                    "firstWishItemId": 1,
                                    "secondWishItemId": 2,
                                    "winnerWishItemId": 1
                                  }
                                ]
                              },
                              "detail": "성공",
                              "code": "COMMON_SUCCESS"
                            }
                        """,
                    ),
                ],
            ),
        ],
    )
    @GetMapping("/{tournamentId}")
    fun getTournamentById(
        @RequestHeader("X-User-Id") userId: UUID,
        @PathVariable tournamentId: Long,
    ): ApiResponseBody<TournamentInfoResponse> {
        val tournamentInfo = tournamentService.getTournamentById(tournamentId, userId)
        return ApiResponseBody.ok(TournamentInfoResponse.from(tournamentInfo))
    }
}
