package com.p2pbet.bet.auction.repository.model

import java.math.BigDecimal

interface AggregatedPoolByAuctionBet {
    val auctionBetId: Long
    val totalPool: BigDecimal
}