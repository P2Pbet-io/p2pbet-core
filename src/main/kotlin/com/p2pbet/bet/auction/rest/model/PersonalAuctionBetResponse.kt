package com.p2pbet.bet.auction.rest.model

import com.p2pbet.bet.auction.entity.AuctionP2PBetEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.bet.common.entity.enums.JoinStatus
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*

data class PersonalAuctionBetResponse(
    override val id: Long,
    override val status: BetStatus,
    override val requestAmount: BigDecimal,
    override val createdBlockNumber: BigInteger,
    override val finalValue: String?,
    override val eventId: UUID,
    override val lockDate: LocalDateTime,
    override val expirationDate: LocalDateTime,
    override val createdDate: LocalDateTime,
    override val executionType: BetExecutionType,
) : AuctionBetResponse(
    id,
    status,
    requestAmount,
    createdBlockNumber,
    finalValue,
    eventId,
    lockDate,
    expirationDate,
    createdDate,
    executionType
) {
    var personalJoins: List<AuctionJoinResponse> = listOf()
    var personalAggregatedStatus: JoinStatus = JoinStatus.JOINED

    companion object {
        fun AuctionP2PBetEntity.toPersonalResponse() = PersonalAuctionBetResponse(
            id = betId,
            status = status,
            requestAmount = requestAmount,
            createdBlockNumber = baseBet.createdBlockNumber,
            finalValue = finalValue,
            eventId = baseEvent.id,
            lockDate = baseBet.lockDate,
            expirationDate = baseBet.expirationDate,
            createdDate = createdDate,
            executionType = baseBet.executionType
        )
    }
}
