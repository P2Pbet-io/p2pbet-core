package com.p2pbet.bet.jackpot.scheduler.expiration.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType

data class JackpotExpirationJobData(
    val id: Long,
    val executionType: BetExecutionType,
)
