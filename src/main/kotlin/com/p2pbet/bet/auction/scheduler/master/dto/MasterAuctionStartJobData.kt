package com.p2pbet.bet.auction.scheduler.master.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.math.BigDecimal
import java.util.*

data class MasterAuctionStartJobData(
    val eventId: UUID,
    val lockPeriod: Long,
    val expirationPeriod: Long,
    val requestAmount: BigDecimal,
    val executionType: BetExecutionType,
)
