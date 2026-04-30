package com.depromeet.team3.tournament.repository

import com.depromeet.team3.tournament.domain.Tournament
import com.depromeet.team3.tournament.domain.TournamentHistory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class TournamentRepositoryImpl(
    private val tournamentJpaRepository: TournamentJpaRepository,
    private val tournamentHistoryJpaRepository: TournamentHistoryJpaRepository,
) : TournamentRepository {
    override fun saveTournament(tournament: Tournament): Long =
        tournamentJpaRepository.save(tournament).getId()

    override fun saveHistory(history: TournamentHistory) {
        tournamentHistoryJpaRepository.save(history)
    }

    override fun findTournamentById(tournamentId: Long): Tournament? =
        tournamentJpaRepository.findByIdOrNull(tournamentId)

    override fun findTournamentHistoriesByTournamentId(tournamentId: Long): List<TournamentHistory> =
        tournamentHistoryJpaRepository.findAllByTournamentIdOrderByCurrentRoundAsc(tournamentId)
}
