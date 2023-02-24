package com.p2pbet.bet.jackpot.scheduler.postclose.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType

data class JackpotPostCloseJobData(
    val id: Long,
    val executionType: BetExecutionType,
)
