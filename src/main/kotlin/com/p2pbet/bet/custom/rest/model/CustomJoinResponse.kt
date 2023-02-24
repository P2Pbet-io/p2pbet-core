package com.p2pbet.bet.custom.rest.model

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import com.p2pbet.bet.custom.entity.CustomJoinEntity
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class CustomJoinResponse(
    val id: UUID,
    val customBetId: Long,
    val executionType: BetExecutionType,
    val client: String,
    val joinId: Long,
    val joinIdClientRef: Long,
    val status: JoinStatus,
    val side: Boolean,
    val joinAmount: BigDecimal,
    val freeAmount: BigDecimal,
    val lockedAmount: BigDecimal,
    val joinHash: String,
    val cancelHash: String?,
    val refundHash: String?,
    val prizeTakenHash: String?,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
) {
    companion object {
        fun CustomJoinEntity.toCustomJoinResponse() = CustomJoinResponse(
            id = id,
            customBetId = customBet.betId,
            executionType = executionType,
            client = client,
            status = status,
            side = side,
            joinHash = joinHash,
            cancelHash = cancelHash,
            refundHash = refundHash,
            prizeTakenHash = prizeTakenHash,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
            joinAmount = joinAmount,
            freeAmount = freeAmount,
            lockedAmount = lockedAmount,
            joinId = joinId,
            joinIdClientRef = joinIdClientRef
        )
    }
}
