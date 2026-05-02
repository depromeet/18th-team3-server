package com.depromeet.team3.tournament.service.dto

data class StartTournament(
    val name: String,
    val round: Int,
    val wishItemIds: List<Long>,
)
