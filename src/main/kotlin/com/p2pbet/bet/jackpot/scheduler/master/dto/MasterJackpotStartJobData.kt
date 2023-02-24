package com.p2pbet.bet.jackpot.scheduler.master.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.math.BigDecimal
import java.util.*

data class MasterJackpotStartJobData(
    val eventId: UUID,
    val lockPeriod: Long,
    val expirationPeriod: Long,
    val requestAmount: BigDecimal,
    val executionType: BetExecutionType,
)
