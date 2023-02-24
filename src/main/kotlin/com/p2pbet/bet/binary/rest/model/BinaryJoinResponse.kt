package com.p2pbet.bet.binary.rest.model

import com.p2pbet.bet.binary.entity.BinaryJoinEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.JoinStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class BinaryJoinResponse(
    val id: UUID,
    val binaryBetId: Long,
    val executionType: BetExecutionType,
    val client: String,
    val joinId: Long,
    val joinIdClientRef: Long,
    val status: JoinStatus,
    val side: Boolean,
    val joinAmount: BigDecimal,
    val joinHash: String,
    val cancelHash: String?,
    val refundHash: String?,
    val prizeTakenHash: String?,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
) {
    companion object {
        fun BinaryJoinEntity.toBinaryJoinResponse() = BinaryJoinResponse(
            id = id,
            binaryBetId = binaryBet.betId,
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
            joinId = joinId,
            joinIdClientRef = joinIdClientRef
        )
    }
}
