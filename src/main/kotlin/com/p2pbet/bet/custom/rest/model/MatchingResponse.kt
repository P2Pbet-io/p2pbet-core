package com.p2pbet.bet.custom.rest.model

import com.p2pbet.bet.custom.entity.CustomMatchingEntity
import java.math.BigDecimal

data class MatchingResponse(
    val leftFreeAmount: BigDecimal,
    val leftLockedAmount: BigDecimal,
    val rightFreeAmount: BigDecimal,
    val rightLockedAmount: BigDecimal,
    val totalPool: BigDecimal,
    val totalBetCount: Long,
) {
    companion object {
        fun CustomMatchingEntity.toMatchingResponse() = MatchingResponse(
            leftFreeAmount = leftFreeAmount,
            leftLockedAmount = leftLockedAmount,
            rightFreeAmount = rightFreeAmount,
            rightLockedAmount = rightLockedAmount,
            totalPool = totalPool,
            totalBetCount = totalJoinCount
        )
    }
}
