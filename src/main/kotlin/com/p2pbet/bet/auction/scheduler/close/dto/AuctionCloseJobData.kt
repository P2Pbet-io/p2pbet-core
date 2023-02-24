package com.p2pbet.bet.auction.scheduler.close.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType
import java.util.*

data class AuctionCloseJobData(
    val id: Long,
    var executionId: UUID? = null,
    val executionType: BetExecutionType,
)
