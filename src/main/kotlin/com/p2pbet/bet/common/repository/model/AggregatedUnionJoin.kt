package com.p2pbet.bet.common.repository.model

import java.math.BigDecimal
import java.time.LocalDateTime

interface AggregatedUnionJoin {
    val id: String
    val betId: Long
    val status: String
    val amount: BigDecimal
    val betType: String
    val client: String
    val joinHash: String
    val cancelHash: String?
    val refundHash: String?
    val prizeTakenHash: String?
    val createdDate: LocalDateTime
    val modifiedDate: LocalDateTime
}