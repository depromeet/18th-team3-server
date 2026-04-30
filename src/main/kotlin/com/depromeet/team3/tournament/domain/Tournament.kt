package com.depromeet.team3.tournament.domain

import com.depromeet.team3.common.domain.LongBaseEntity
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.OrderColumn
import java.util.UUID

@Entity
class Tournament(
    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    val userId: UUID,
    val name: String,
    val round: Int,
    @ElementCollection
    @CollectionTable(name = "tournament_wish_item", joinColumns = [JoinColumn(name = "tournament_id")])
    @Column(name = "wish_item_id")
    @OrderColumn(name = "position")
    val wishItemIds: List<Long>,
    var finalWinnerWishItemId: Long? = null,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    var status: TournamentStatus = TournamentStatus.IN_PROGRESS,
) : LongBaseEntity() {
    fun complete(winnerWishItemId: Long) {
        this.finalWinnerWishItemId = winnerWishItemId
        this.status = TournamentStatus.COMPLETED
    }

    fun isFinalRound(currentRound: Int): Boolean = currentRound == FINAL_ROUND_SIZE

    fun isCompleted(): Boolean = status == TournamentStatus.COMPLETED

    companion object {
        private const val FINAL_ROUND_SIZE = 2
    }
}
