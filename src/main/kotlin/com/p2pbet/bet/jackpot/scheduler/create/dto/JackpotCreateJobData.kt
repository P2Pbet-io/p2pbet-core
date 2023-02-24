package com.p2pbet.bet.jackpot.scheduler.create.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.math.BigDecimal
import java.util.*

data class JackpotCreateJobData(
    val eventId: UUID,
    val lockPeriod: Long,
    val expirationPeriod: Long,
    val requestAmount: BigDecimal,
    var executionId: UUID? = null,
    val executionType: BetExecutionType,
)
