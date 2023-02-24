package com.p2pbet.bet.custom.rest.model

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import com.p2pbet.bet.common.entity.enums.BetStatus
import com.p2pbet.bet.custom.entity.CustomP2PBetEntity
import com.p2pbet.bet.custom.rest.model.MatchingResponse.Companion.toMatchingResponse
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*

data class CustomBetResponse(
    val id: Long,
    val status: BetStatus,
    val createdBlockNumber: BigInteger,
    val targetValue: String,
    val targetSide: Boolean,
    val finalValue: String?,
    val targetSideWon: Boolean?,
    val eventId: UUID,
    val coefficient: BigDecimal,
    val lockDate: LocalDateTime,
    val expirationDate: LocalDateTime,
    val createdDate: LocalDateTime,
    val hidden: Boolean,
    val matchingInfo: MatchingResponse,
    val executionType: BetExecutionType,
) {
    companion object {
        fun CustomP2PBetEntity.toCustomBetResponse() = CustomBetResponse(
            id = betId,
            status = status,
            targetValue = targetValue,
            targetSide = targetSide,
            createdBlockNumber = baseBet.createdBlockNumber,
            finalValue = finalValue,
            targetSideWon = targetSideWon,
            eventId = baseEvent.id,
            lockDate = baseBet.lockDate,
            expirationDate = baseBet.expirationDate,
            createdDate = createdDate,
            hidden = hidden,
            matchingInfo = matchingInfo!!.toMatchingResponse(),
            coefficient = coefficient,
            executionType = baseBet.executionType
        )
    }
}
