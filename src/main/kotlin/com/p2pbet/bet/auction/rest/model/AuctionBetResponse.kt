package com.p2pbet.bet.auction.rest.model

import com.p2pbet.bet.auction.entity.AuctionP2PBetEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*

open class AuctionBetResponse(
    open val id: Long,
    open val status: BetStatus,
    open val requestAmount: BigDecimal,
    open val createdBlockNumber: BigInteger,
    open val finalValue: String?,
    open val eventId: UUID,
    open val lockDate: LocalDateTime,
    open val expirationDate: LocalDateTime,
    open val createdDate: LocalDateTime,
    open val executionType: BetExecutionType,
) {
    var totalPool: BigDecimal = BigDecimal.ZERO

    companion object {
        fun AuctionP2PBetEntity.toResponse() = AuctionBetResponse(
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
