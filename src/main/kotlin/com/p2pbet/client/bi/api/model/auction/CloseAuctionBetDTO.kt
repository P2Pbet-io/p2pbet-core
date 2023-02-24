package com.p2pbet.client.bi.api.model.auction

import java.math.BigInteger

data class CloseAuctionBetDTO(
    val betId: Long,
    val finalValue: String,
    val joinIdsWon: List<BigInteger>,
)