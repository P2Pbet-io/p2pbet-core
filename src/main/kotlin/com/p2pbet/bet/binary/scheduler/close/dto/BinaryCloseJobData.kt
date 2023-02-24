package com.p2pbet.bet.binary.scheduler.close.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.util.*

data class BinaryCloseJobData(
    val id: Long,
    var executionId: UUID? = null,
    val executionType: BetExecutionType,
)
