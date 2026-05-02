package com.depromeet.team3.tournament.service.dto

data class RecordMatch(
    val tournamentId: Long,
    val currentRound: Int,
    val firstWishItemId: Long,
    val secondWishItemId: Long,
    val winnerWishItemId: Long,
)
