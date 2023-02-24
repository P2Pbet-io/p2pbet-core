package com.p2pbet.bet.jackpot.scheduler.close.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.util.*

data class JackpotCloseJobData(
    val id: Long,
    var executionId: UUID? = null,
    val executionType: BetExecutionType,
)
