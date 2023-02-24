package com.p2pbet.bet.custom.repository.model

import java.math.BigDecimal

interface AggregationByEventCustom {
    val eventBaseId: String
    val sum: BigDecimal
    val count: Int
}