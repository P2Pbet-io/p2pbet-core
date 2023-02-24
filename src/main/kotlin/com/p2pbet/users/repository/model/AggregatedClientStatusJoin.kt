package com.p2pbet.users.repository.model

import java.math.BigDecimal

interface AggregatedClientStatusJoin {
    val joinStatus: String
    val totalJoin: BigDecimal
    val expectedWonAmount: BigDecimal
    val wonAmountTaken: BigDecimal
}