package com.p2pbet.bet.auction.scheduler.expiration.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType

data class AuctionExpirationJobData(
    val id: Long,
    val executionType: BetExecutionType,
)
