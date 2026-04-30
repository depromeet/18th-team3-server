package com.depromeet.team3.tournament.service

import com.depromeet.team3.tournament.domain.Tournament
import com.depromeet.team3.tournament.repository.TournamentRepository
import com.depromeet.team3.tournament.service.dto.StartTournament
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TournamentService(
    private val tournamentRepository: TournamentRepository,
) {
    @Transactional
    fun start(userId: UUID, command: StartTournament): Long {
        return tournamentRepository.saveTournament(
            Tournament(
                userId = userId,
                name = command.name,
                round = command.round,
                wishItemIds = command.wishItemIds,
            ),
        )
    }
}
