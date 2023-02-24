package com.p2pbet.bet.custom.scheduler.expiration.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType

data class CustomExpirationJobData(
    val id: Long,
    val executionType: BetExecutionType,
)
