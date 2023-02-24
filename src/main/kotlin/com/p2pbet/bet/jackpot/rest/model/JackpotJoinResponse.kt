package com.p2pbet.bet.jackpot.rest.model

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.jackpot.entity.JackpotJoinEntity
import com.p2pbet.bet.jackpot.entity.enums.JackpotJoinStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class JackpotJoinResponse(
    val id: UUID,
    val auctionBetId: Long,
    val executionType: BetExecutionType,
    val client: String,
    val joinId: Long,
    val joinIdClientRef: Long,
    val status: JackpotJoinStatus,
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
        fun JackpotJoinEntity.toJackpotJoinResponse() = JackpotJoinResponse(
            id = id,
            auctionBetId = jackpotBet.betId,
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
            joinAmount = jackpotBet.requestAmount,
            joinId = joinId,
            joinIdClientRef = joinIdClientRef
        )
    }
}
