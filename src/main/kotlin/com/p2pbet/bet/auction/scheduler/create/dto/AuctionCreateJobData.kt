package com.p2pbet.bet.auction.scheduler.create.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.math.BigDecimal
import java.util.*

data class AuctionCreateJobData(
    val eventId: UUID,
    val lockPeriod: Long,
    val expirationPeriod: Long,
    val requestAmount: BigDecimal,
    var executionId: UUID? = null,
    val executionType: BetExecutionType,
)
