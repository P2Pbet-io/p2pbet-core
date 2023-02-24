package com.p2pbet.bet.auction.rest.model

import com.p2pbet.bet.auction.entity.AuctionJoinEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class AuctionJoinResponse(
    val id: UUID,
    val auctionBetId: Long,
    val executionType: BetExecutionType,
    val client: String,
    val joinId: Long,
    val joinIdClientRef: Long,
    val status: JoinStatus,
    val targetValue: String,
    val joinAmount: BigDecimal,
    val joinHash: String,
    val cancelHash: String?,
    val refundHash: String?,
    val prizeTakenHash: String?,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
) {
    companion object {
        fun AuctionJoinEntity.toAuctionJoinResponse() = AuctionJoinResponse(
            id = id,
            auctionBetId = auctionBet.betId,
            executionType = executionType,
            client = client,
            status = status,
            targetValue = targetValue,
            joinHash = joinHash,
            cancelHash = cancelHash,
            refundHash = refundHash,
            prizeTakenHash = prizeTakenHash,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
            joinAmount = auctionBet.requestAmount,
            joinId = joinId,
            joinIdClientRef = joinIdClientRef
        )

        fun List<AuctionJoinResponse>.getAggregatedStatus(): JoinStatus =
            this.foldRight(JoinStatus.JOINED) { value, acc ->
                if (value.status.ordinal > acc.ordinal) {
                    value.status
                } else {
                    acc
                }
            }
    }
}
