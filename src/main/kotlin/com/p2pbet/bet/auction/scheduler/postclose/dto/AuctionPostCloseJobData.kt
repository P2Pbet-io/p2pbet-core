package com.p2pbet.bet.auction.scheduler.postclose.dto

import com.p2pbet.bet.common.entity.enums.BetExecutionType

data class AuctionPostCloseJobData(
    val id: Long,
    val executionType: BetExecutionType,
)
