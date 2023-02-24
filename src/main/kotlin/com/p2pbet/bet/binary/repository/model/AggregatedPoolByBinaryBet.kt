package com.p2pbet.bet.binary.repository.model

import java.math.BigDecimal

interface AggregatedPoolByBinaryBet {
    val binaryBetId: Long
    val side: Boolean
    val pool: BigDecimal
    val count: Long
}