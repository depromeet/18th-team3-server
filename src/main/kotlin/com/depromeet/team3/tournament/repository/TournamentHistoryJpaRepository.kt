package com.depromeet.team3.tournament.repository

import com.depromeet.team3.tournament.domain.TournamentHistory
import org.springframework.data.jpa.repository.JpaRepository

interface TournamentHistoryJpaRepository : JpaRepository<TournamentHistory, Long> {
    fun findAllByTournamentIdOrderByCurrentRoundAscIdAsc(tournamentId: Long): List<TournamentHistory>
}
