package com.p2pbet.bet.jackpot.rest.model

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.bet.jackpot.entity.JackpotP2PBetEntity
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*

data class JackpotBetResponse(
    val id: Long,
    val status: BetStatus,
    val requestAmount: BigDecimal,
    val createdBlockNumber: BigInteger,
    val finalValue: String?,
    val eventId: UUID,
    val lockDate: LocalDateTime,
    val expirationDate: LocalDateTime,
    val createdDate: LocalDateTime,
    val startBank: BigDecimal,
    val totalRaffled: BigDecimal?,
    val firstWonSize: Long?,
    val secondWonSize: Long?,
    val thirdWonSize: Long?,
    val executionType: BetExecutionType,
) {
    var totalPool: BigDecimal = BigDecimal.ZERO

    companion object {
        fun JackpotP2PBetEntity.toResponse() = JackpotBetResponse(
            id = betId,
            status = status,
            requestAmount = requestAmount,
            createdBlockNumber = baseBet.createdBlockNumber,
            finalValue = finalValue,
            eventId = baseEvent.id,
            lockDate = baseBet.lockDate,
            expirationDate = baseBet.expirationDate,
            createdDate = createdDate,
            startBank = startBank,
            totalRaffled = totalRaffled,
            firstWonSize = firstWonSize,
            secondWonSize = secondWonSize,
            thirdWonSize = thirdWonSize,
            executionType = baseBet.executionType
        )
    }
}
