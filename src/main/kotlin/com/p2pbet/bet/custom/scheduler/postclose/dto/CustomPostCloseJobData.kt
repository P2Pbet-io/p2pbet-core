package com.p2pbet.bet.custom.scheduler.postclose.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType

data class CustomPostCloseJobData(
    val id: Long,
    val executionType: BetExecutionType,
)
