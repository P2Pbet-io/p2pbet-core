package com.p2pbet.bet.binary.scheduler.postclose.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType

data class BinaryPostCloseJobData(
    val id: Long,
    val executionType: BetExecutionType,
)
