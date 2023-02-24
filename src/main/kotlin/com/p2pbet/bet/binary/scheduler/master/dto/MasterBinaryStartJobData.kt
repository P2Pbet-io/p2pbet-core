package com.p2pbet.bet.binary.scheduler.master.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.util.*

data class MasterBinaryStartJobData(
    val eventId: UUID,
    val lockPeriod: Long,
    val expirationPeriod: Long,
    val executionType: BetExecutionType,
)
