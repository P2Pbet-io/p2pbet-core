package com.p2pbet.bet.binary.rest.model

import com.p2pbet.bet.binary.entity.BinaryP2PBetEntity
import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*

data class BinaryBetResponse(
    val id: Long,
    val primaryId: UUID,
    val status: BetStatus,
    val createdBlockNumber: BigInteger,
    val lockedValue: String?,
    val finalValue: String?,
    val sideWon: Boolean?,
    val eventId: UUID,
    val lockDate: LocalDateTime,
    val expirationDate: LocalDateTime,
    val createdDate: LocalDateTime,
    val executionType: BetExecutionType,
) {
    var leftPool: BigDecimal = BigDecimal.ZERO
    var rightPool: BigDecimal = BigDecimal.ZERO
    var leftCount: Long = 0
    var rightCount: Long = 0

    companion object {
        fun BinaryP2PBetEntity.toResponse() = BinaryBetResponse(
            id = betId,
            primaryId = id,
            status = status,
            createdBlockNumber = baseBet.createdBlockNumber,
            lockedValue = lockedValue,
            finalValue = finalValue,
            sideWon = sideWon,
            eventId = baseEvent.id,
            lockDate = baseBet.lockDate,
            expirationDate = baseBet.expirationDate,
            createdDate = createdDate,
            executionType = baseBet.executionType
        )
    }
}
