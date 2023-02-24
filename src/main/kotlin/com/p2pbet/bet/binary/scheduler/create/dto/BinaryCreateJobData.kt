package com.p2pbet.bet.binary.scheduler.create.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.util.*

data class BinaryCreateJobData(
    val eventId: UUID,
    val lockPeriod: Long,
    val expirationPeriod: Long,
    var executionId: UUID? = null,
    val executionType: BetExecutionType,
)
