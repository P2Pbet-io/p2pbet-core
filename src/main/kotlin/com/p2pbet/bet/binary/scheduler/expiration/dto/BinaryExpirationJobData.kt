package com.p2pbet.bet.binary.scheduler.expiration.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType

data class BinaryExpirationJobData(
    val id: Long,
    val executionType: BetExecutionType,
)
