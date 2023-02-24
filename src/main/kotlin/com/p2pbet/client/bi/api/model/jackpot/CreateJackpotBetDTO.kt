package com.p2pbet.client.bi.api.model.jackpot

import java.math.BigDecimal
import java.time.OffsetDateTime

class CreateJackpotBetDTO(
    val eventId: String,
    val lockTime: OffsetDateTime,
    val expirationTime: OffsetDateTime,
    val requestAmount: BigDecimal,
)